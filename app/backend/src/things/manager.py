from database import get_async_session
from models import UserORM, GroupORM
from fastapi import Depends
from auth.base_config import current_user
from sqlalchemy import select


async def get_curr_user_group(
        user: UserORM = Depends(current_user)
):
    async for session in get_async_session():
        query_group_id = await session.execute(
            select(GroupORM.id).where(GroupORM.owner_id == user.id)
        )
        return query_group_id.scalar()

