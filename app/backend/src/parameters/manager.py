from database import get_async_session
from models import UserORM, ThingORM, UserGroupORM, ParameterORM, ThingParameterORM
from fastapi import HTTPException
from sqlalchemy import select, and_, delete

from parameters.schemas import ParameterRead, ParameterCreate, ParameterThingRead, ParameterDelete


async def get_thing_by_title(
    thing_title: str,
    user: UserORM
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


async def get_parameters_by_thing_title_logic(
        thing_title: str,
        session,
        user: UserORM):
    thing = await get_thing_by_title(thing_title, user)
    if thing is None:
        raise HTTPException(status_code=500, detail="Didn't find such a thing")

    result = await session.execute(
        select(ParameterORM).join(ThingParameterORM).where(ThingParameterORM.thing_id == thing.id)
    )
    parameters = result.scalars().all()
    if parameters:
        return [ParameterRead.from_orm(parameter).dict() for parameter in parameters]


async def parameter_create_logic(
        thing_title: str,
        parameter: ParameterCreate,
        session,
        user: UserORM):
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
        return ParameterThingRead.from_orm(new_thing_param).dict()
    else:
        raise HTTPException(status_code=400, detail="No data found")


async def delete_parameter_logic(
        par_del: ParameterDelete,
        session,
        user: UserORM):
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

    return {"status": "success"}