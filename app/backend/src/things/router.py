from typing import Optional, List
from .schema import ThingRead, ThingCreate
from sqlalchemy import select, delete
from fastapi import APIRouter, Depends
from models import ThingORM
from database import get_async_session

router_thing = APIRouter(
    prefix="/thing",
    tags=["Thing"],
)


@router_thing.get("/get_by_name/{thing_name}")
async def get_by_name_thing(thing_name: str, session=Depends(get_async_session)) -> Optional[List[ThingRead]]:
    try:
        async with session:
            query = select(ThingORM).where(ThingORM.title.like(f"%{thing_name}%"))
            result = await session.execute(query)
            thing_orms = result.scalars().all()
            if thing_orms:
                return [ThingRead.from_orm(thing_orm) for thing_orm in thing_orms]
            return None
    except Exception as e:
        print(f"An error occurred: {e}")
        return None


@router_thing.post("/add_thing")
async def add_thing(thing: ThingCreate, session=Depends(get_async_session)):
    async with session:
        new_thing = ThingORM(**thing.dict())
        session.add(new_thing)
        await session.commit()
        return {"status": "success"}


@router_thing.delete("/delete_thing_by_name/{name}")
async def delete_thing(name: str, session=Depends(get_async_session)):
    async with session:
        res = delete(ThingORM).where(ThingORM.title == name)
        await session.execute(res)
        await session.commit()
        return {"status": "success"}
