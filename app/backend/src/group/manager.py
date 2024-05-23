import datetime
from sqlalchemy import delete, select
from models import GroupORM, UserORM
from .schemas import UserEmptyGroupCreate
from fastapi.responses import JSONResponse
from fastapi import HTTPException
from database import get_async_session


async def create_group(
        user: UserEmptyGroupCreate):
    try:
        async for session in get_async_session():
            new_group = GroupORM(
                title=user.username,
                url=f"{user.username}+{datetime.datetime.utcnow()}",
                owner_id=user.user_id
            )
            session.add(new_group)
            await session.commit()
            if new_group.id is not None:
                return JSONResponse(status_code=200, content={"status": "success"})
            else:
                raise HTTPException(status_code=400, detail="I have not created a group")
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


async def delete_user_groups(username: str):
    try:
        async for session in get_async_session():
            statement = select(UserORM).where(UserORM.username == username)
            results = await session.execute(statement)
            user = results.unique().scalar_one_or_none()
            stmt = delete(GroupORM).where(GroupORM.owner_id == user.id)
            await session.execute(stmt)
            await session.commit()
            return JSONResponse(status_code=200, content={"status": "success"})
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
