from parameters.manager import get_thing_by_title
from .manager import get_curr_user_group
from .schemas import ThingRead, ThingCreate, UserGroupRead
from sqlalchemy import select, delete, and_, update
from fastapi import APIRouter, Depends, HTTPException
from models import ThingORM, UserORM, GroupORM, UserGroupORM
from database import get_async_session
from fastapi.responses import JSONResponse
from auth.base_config import current_user

router_thing = APIRouter(
    prefix="/thing",
    tags=["Thing"],
)


# Робит
# Для поиска
@router_thing.get("/get_by_name/{thing_name}")
async def get_by_name(
        thing_name: str,
        session=Depends(get_async_session),
        user: UserORM = Depends(current_user)
) -> JSONResponse:
    try:
        async with session:
            query = select(ThingORM).join(
                UserGroupORM
            ).where(
                and_(
                    ThingORM.title.like(f"%{thing_name}%"),
                    UserGroupORM.user_id == user.id)
            )
            result = await session.execute(query)
            thing_orms = result.scalars().all()

            if thing_orms:
                result = [ThingRead.from_orm(thing_orm).dict() for thing_orm in thing_orms]
                return JSONResponse(status_code=200, content={"status": "success", "data": result})
            else:
                raise HTTPException(status_code=400, detail="No data found")
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


# Робит
@router_thing.post("/add_thing")
async def add_thing(
        thing: ThingCreate,
        session=Depends(get_async_session),
        user: UserORM = Depends(current_user)
) -> JSONResponse:
    try:
        async with session:
            query = await session.execute(
                select(GroupORM.id).where(GroupORM.owner_id == user.id)
            )
            group_id = query.scalar()

            new_thing = ThingORM(**thing.dict())
            session.add(new_thing)
            await session.commit()
            await session.refresh(new_thing)

            add_in_user_group = UserGroupORM(
                group_id=group_id,
                thing_id=new_thing.id,
                user_id=user.id)
            session.add(add_in_user_group)
            await session.commit()

            return JSONResponse(
                status_code=200,
                content={"status": "success",
                         "data": UserGroupRead.from_orm(add_in_user_group).dict()}
            )

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


# Робит
@router_thing.delete("/delete_thing_by_title/{title}")
async def delete_thing(
        title: str,
        session=Depends(get_async_session),
        user: UserORM = Depends(current_user)
) -> JSONResponse:
    try:
        async with session:
            query_thing_id = await session.execute(
                select(ThingORM.id).where(ThingORM.title == title)
            )
            thing_id = query_thing_id.scalar()
            group_id = await get_curr_user_group(user)

            del_in_usr_group = delete(UserGroupORM).where(and_(
                UserGroupORM.thing_id == thing_id,
                UserGroupORM.group_id == group_id
            ))
            res = delete(ThingORM).where(ThingORM.title == title)

            await session.execute(del_in_usr_group)
            await session.execute(res)
            await session.commit()
            return JSONResponse(
                status_code=200,
                content={"status": "success"}
            )

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router_thing.get("/get_all_thing_for_user/")
async def get_all_thing_for_user(
        session=Depends(get_async_session),
        user: UserORM = Depends(current_user)
) -> JSONResponse:
    try:
        async with session:
            group_id = await get_curr_user_group(user)
            query = select(ThingORM).join(
                UserGroupORM
            ).where(UserGroupORM.group_id == group_id)
            result = await session.execute(query)
            thing_orms = result.scalars().all()
            if thing_orms:
                result = [ThingRead.from_orm(thing_orm).dict() for thing_orm in thing_orms]
                return JSONResponse(status_code=200, content={"status": "success", "data": result})
            else:
                raise HTTPException(status_code=400, detail="No data found")
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router_thing.put("/update_name/{thing_title}/{new_title}")
async def update_name(
        thing_title: str,
        new_title: str,
        session=Depends(get_async_session),
        user: UserORM = Depends(current_user)
):
    try:
        async with session:
            thing = await get_thing_by_title(thing_title, user)

            stmt = update(ThingORM).where(
                ThingORM.id == thing.id
            ).values(
                title=new_title,
            )
            await session.execute(stmt)
            await session.commit()

            return JSONResponse(status_code=200, content={"status": "success"})

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
