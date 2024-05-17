from sqlalchemy import select, update, join, and_, delete
from fastapi import APIRouter, Depends, HTTPException
from fastapi.responses import JSONResponse
from auth.base_config import current_user
from models import ParameterORM, ThingParameterORM, UserORM
from database import get_async_session
from .manager import get_thing_by_title, get_parameter
from .schemas import ParameterRead, ParameterUpdate, ParameterCreate, ParameterDelete, ParameterThingRead, \
    ParameterAuthoriz

router_parameter = APIRouter(
    prefix="/parameter",
    tags=["Parameter"],
)


# Нужно написать для всех unit тесты
# Робит
@router_parameter.post("/parameter_create/{thing_title}")
async def parameter_create(
        thing_title: str,
        parameter: ParameterCreate,
        session=Depends(get_async_session),
        user: UserORM = Depends(current_user),
) -> JSONResponse:
    try:
        async with session:
            new_param = ParameterORM(**parameter.dict())
            session.add(new_param)
            await session.commit()

            thing_id = await get_thing_by_title(thing_title, user)
            if thing_id is None:
                raise ValueError(f"Thing with title '{thing_title}' not found")

            new_thing_param = ThingParameterORM(thing_id=thing_id.id, parameter_id=new_param.id)
            session.add(new_thing_param)
            await session.commit()
            if new_thing_param:
                return JSONResponse(status_code=200,
                                    content={"status": "success",
                                             "data": ParameterThingRead.from_orm(new_thing_param).dict()})
            else:
                raise HTTPException(status_code=400, detail="No data found")
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


# Робит
@router_parameter.get("/get_parameters_by_thing_name/{thing_title}")
async def parameters_get_by_thing_title(
        thing_name: str,
        session=Depends(get_async_session),
        user: UserORM = Depends(current_user)
) -> JSONResponse:
    try:
        async with session:
            thing = await get_thing_by_title(thing_name, user)
            if thing is None:
                raise HTTPException(status_code=500, detail="Didn't find such a thing")

            result = await session.execute(
                select(ParameterORM).join(ThingParameterORM).where(ThingParameterORM.thing_id == thing.id)
            )
            parameters = result.scalars().all()
            if parameters:
                result = [ParameterRead.from_orm(parameter).dict() for parameter in parameters]
                return JSONResponse(status_code=200, content={"status": "success", "data": result})
            else:
                raise HTTPException(status_code=400, detail="No data found")
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


# Робит
@router_parameter.delete("/delete_parameter")
async def delete_parameter(
        par_del: ParameterDelete,
        session=Depends(get_async_session),
        user: UserORM = Depends(current_user)
) -> JSONResponse:
    try:
        async with session:
            thing = await get_thing_by_title(par_del.thing_title, user)
            parameter = await get_parameter(thing.id, par_del.key, par_del.value)

            del_thing_param = delete(ThingParameterORM).where(
                and_(
                    ThingParameterORM.thing_id == thing.id,
                    ThingParameterORM.parameter_id == parameter.id
                )
            )
            del_param = delete(ParameterORM).where(ParameterORM.id == parameter.id)

            await session.execute(del_thing_param)
            await session.execute(del_param)
            await session.commit()

            return JSONResponse(status_code=200, content={"status": "success"})
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


# Робит
@router_parameter.put("/update/{parameter_id}")
async def parameter_update(
        old_param: ParameterAuthoriz,
        new_parameter: ParameterUpdate,
        session=Depends(get_async_session),
        user: UserORM = Depends(current_user)
) -> JSONResponse:
    try:
        async with session:
            thing = await get_thing_by_title(old_param.thing_title, user)
            get_old_parameter = await get_parameter(thing.id, old_param.key, old_param.value)

            stmt = update(ParameterORM).where(
                ParameterORM.id == get_old_parameter.id
            ).values(
                key=new_parameter.key,
                value=new_parameter.value
            )
            await session.execute(stmt)
            await session.commit()

            return JSONResponse(status_code=200, content={"status": "success"})
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
