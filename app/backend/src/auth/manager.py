import contextlib
from typing import Optional

from fastapi import Depends, Request
from fastapi_users import (BaseUserManager, IntegerIDMixin, exceptions, models,
                           schemas)
from sqlalchemy import select, delete, Select
from models import UserORM
from auth.utils import get_user_db
from config import SECRET_AUTH
from database import get_async_session
from sqlalchemy.ext.asyncio import AsyncSession
from .schemas import UserRead
from auth.schemas import UserCreate
from fastapi_users.exceptions import UserAlreadyExists


async def get_user_manager(user_db=Depends(get_user_db)):
    yield UserManager(user_db)


get_async_session_context = contextlib.asynccontextmanager(get_async_session)
get_user_db_context = contextlib.asynccontextmanager(get_user_db)
get_user_manager_context = contextlib.asynccontextmanager(get_user_manager)


async def create_user(email: str, username: str, password: str):
    try:
        async with get_async_session_context() as session:
            async with get_user_db_context(session) as user_db:
                async with get_user_manager_context(user_db) as user_manager:
                    user = await user_manager.create(
                        UserCreate(
                            email=email, username=username, password=password
                        )
                    )
                    print(f"User created {user}")
                    return user
    except UserAlreadyExists:
        print(f"User {email} already exists")
        raise


async def delete_by_username(username: str) -> int:
    async with get_async_session_context() as session:
        request = (
            delete(UserORM)
            .where(UserORM.username == username)
        )
        result = await session.execute(request)
        if result.rowcount == 0:
            return 1
        await session.commit()
        return 0


async def get_by_id(user_id: int) -> Optional[UserRead]:
    async with get_async_session_context() as session:
        statement = select(UserORM).where(UserORM.id == user_id)
        user_orm = await _get_user(statement, session)
        if user_orm is not None:
            return UserRead.from_orm(user_orm)


async def get_by_username(username) -> Optional[UserRead]:
    async with get_async_session_context() as session:
        statement = select(UserORM).where(UserORM.username == username)
        user_orm = await _get_user(statement, session)
        if user_orm is not None:
            return UserRead.from_orm(user_orm)


async def _get_user(statement: Select, session: AsyncSession):
    results = await session.execute(statement)
    return results.unique().scalar_one_or_none()


class UserManager(IntegerIDMixin, BaseUserManager[UserORM, int]):
    reset_password_token_secret = SECRET_AUTH
    verification_token_secret = SECRET_AUTH

    async def on_after_register(self, user: UserORM,
                                request: Optional[Request] = None
                                ) -> None:
        print(f"User {user.id} has registered.")

    async def create(
            self,
            user_create: schemas.UC,
            safe: bool = False,
            request: Optional[Request] = None,
    ) -> models.UP:
        await self.validate_password(user_create.password, user_create)

        existing_user_by_email = await self.user_db.get_by_email(user_create.email)
        existing_user_by_username = await get_by_username(user_create.username)

        if existing_user_by_email or existing_user_by_username is not None:
            raise exceptions.UserAlreadyExists()

        user_dict = (
            user_create.create_update_dict()
            if safe
            else user_create.create_update_dict_superuser()
        )
        password = user_dict.pop("password")
        user_dict["hashed_password"] = self.password_helper.hash(password)

        created_user = await self.user_db.create(user_dict)

        await self.on_after_register(created_user, request)

        return created_user
