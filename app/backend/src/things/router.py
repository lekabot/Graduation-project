from parameters.manager import get_thing_by_title
from .manager import get_curr_user_group, add_thing_logic, delete_thing_logic
from .schemas import ThingRead
from sqlalchemy import select, delete, and_, update
from fastapi import APIRouter, Depends, HTTPException
from models import ThingORM, UserORM, UserGroupORM
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


@router_thing.post("/add_thing/{thing_title}")
async def add_thing(
        thing_title: str,
        session=Depends(get_async_session),
        user: UserORM = Depends(current_user)
) -> JSONResponse:
    try:
        async with session:
            result = await add_thing_logic(thing_title, session, user)
            return JSONResponse(
                status_code=200,
                content={"status": "success", "data": result}
            )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router_thing.delete("/delete_thing_by_title/{title}")
async def delete_thing(
        title: str,
        session=Depends(get_async_session),
        user: UserORM = Depends(current_user)
) -> JSONResponse:
    try:
        async with session:
            result = await delete_thing_logic(title, session, user)
            return JSONResponse(
                status_code=200,
                content=result
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
