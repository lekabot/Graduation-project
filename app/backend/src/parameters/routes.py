from sqlalchemy import update
from fastapi import APIRouter, Depends, HTTPException
from fastapi.responses import JSONResponse
from auth.base_config import current_user
from models import ParameterORM, ThingParameterORM, UserORM
from database import get_async_session
from .manager import get_thing_by_title, get_parameter, get_parameters_by_thing_title_logic, parameter_create_logic, \
    delete_parameter_logic
from .schemas import ParameterUpdate, ParameterCreate, ParameterDelete, ParameterAuthoriz

router_parameter = APIRouter(
    prefix="/parameter",
    tags=["Parameter"],
)


@router_parameter.post("/parameter_create/{thing_title}")
async def parameter_create(
        thing_title: str,
        parameter: ParameterCreate,
        session=Depends(get_async_session),
        user: UserORM = Depends(current_user),
) -> JSONResponse:
    try:
        async with session:
            result = await parameter_create_logic(thing_title, parameter, session, user)
            return JSONResponse(
                status_code=200,
                content={"status": "success", "data": result}
            )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router_parameter.get("/get_parameters_by_thing_name/{thing_title}")
async def parameters_get_by_thing_title(
        thing_name: str,
        session=Depends(get_async_session),
        user: UserORM = Depends(current_user)
) -> JSONResponse:
    try:
        async with session:
            result = await get_parameters_by_thing_title_logic(thing_name, session, user)
            return JSONResponse(
                status_code=200,
                content={"status": "success", "data": result}
            )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router_parameter.delete("/delete_parameter")
async def delete_parameter(
        par_del: ParameterDelete,
        session=Depends(get_async_session),
        user: UserORM = Depends(current_user)
) -> JSONResponse:
    try:
        async with session:
            result = await delete_parameter_logic(par_del, session, user)
            return JSONResponse(
                status_code=200,
                content=result
            )
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
