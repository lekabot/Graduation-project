from sqlalchemy import select
from fastapi import APIRouter, Depends
from models import GroupORM
from database import get_async_session
from .schemas import UserEmptyGroupCreate, UserGroupRead
from fastapi import HTTPException
from fastapi.responses import JSONResponse
from .manager import create_group, delete_user_groups

group_router = APIRouter(
    prefix="/group",
    tags=["Group"],
)


@group_router.post("/create_empty")
async def create_empty_endpoint(
        user: UserEmptyGroupCreate):
    return await create_group(user)


@group_router.delete("/delete_by_username/{username}")
async def delete_by_username(
        username: str):
    return await delete_user_groups(username)


@group_router.get("/get_group_by_user_id/{owner_id}")
async def get_group_by_user_id(
        owner_id: int,
        session=Depends(get_async_session)):
    async with session.begin():
        query = await session.execute(
            select(GroupORM).where(GroupORM.owner_id == owner_id)
        )
        group_orms = query.scalars().all()
        if group_orms:
            result = [UserGroupRead.from_orm(group_orm).dict() for group_orm in group_orms]
            return JSONResponse(status_code=200, content={"status": "success", "data": result})
        else:
            raise HTTPException(status_code=400, detail="No data found")


