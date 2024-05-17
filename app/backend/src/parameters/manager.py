from database import get_async_session
from models import UserORM, ThingORM, UserGroupORM, ParameterORM, ThingParameterORM
from fastapi import Depends
from auth.base_config import current_user
from sqlalchemy import select, and_


async def get_thing_by_title(
    thing_title: str,
    user: UserORM = Depends(current_user),
):
    async for session in get_async_session():
        query = select(ThingORM).join(
            UserGroupORM
        ).where(
            and_(
                ThingORM.title == thing_title,
                UserGroupORM.user_id == user.id)
        )
        result = await session.execute(query)
        return result.scalars().first()


async def get_parameter(
    thing_id: int,
    key: str,
    value: str
):
    async for session in get_async_session():
        query = select(ParameterORM).join(ThingParameterORM).where(
            and_(ThingParameterORM.thing_id == thing_id,
                 ParameterORM.key == key,
                 ParameterORM.value == value))
        result = await session.execute(query)
        return result.scalar_one()
