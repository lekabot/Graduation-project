from sqlalchemy import select, delete
from fastapi import APIRouter, Depends
from models import GroupORM, UserORM
from database import get_async_session
from .schemas import UserEmptyGroupCreate, UserGroupRead
from fastapi import HTTPException
from fastapi.responses import JSONResponse
from .manager import create_group

router_group = APIRouter(
    prefix="/group",
    tags=["Group"],
)


@router_group.post("/create_empty")
async def create_empty_endpoint(
        user: UserEmptyGroupCreate):
    return await create_group(user)


@router_group.delete("/delete_by_username/{username}")
async def delete_by_username(
        username: str,
        session=Depends(get_async_session)):
    async with session:
        query = await session.execute(
            select(UserORM.id).where(UserORM.username == username)
        )
        user_id = query.scalar()
        if user_id is None:
            raise HTTPException(status_code=400, detail="User not found")
        stmt = delete(GroupORM).where(GroupORM.owner_id == user_id)
        result = await session.execute(stmt)
        await session.commit()
        if result.rowcount > 0:
            return JSONResponse(status_code=200, content={"status": "success"})
        else:
            raise HTTPException(status_code=400, detail="No data found")


@router_group.get("/get_group_by_user_id/{owner_id}")
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


