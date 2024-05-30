from http.client import HTTPException

from database import get_async_session
from models import UserORM, GroupORM, ThingORM, UserGroupORM, ThingParameterORM
from sqlalchemy import select, delete, and_

from parameters.manager import get_thing_by_title, get_parameters_by_thing_title_logic, delete_parameter_logic
from parameters.schemas import ParameterDelete
from things.schemas import UserGroupRead


async def get_curr_user_group(
        user: UserORM
):
    async for session in get_async_session():
        query_group_id = await session.execute(
            select(GroupORM.id).where(GroupORM.owner_id == user.id)
        )
        return query_group_id.scalar()


async def add_thing_logic(
        thing_title: str,
        session,
        user: UserORM):
    query = await session.execute(
        select(GroupORM.id).where(GroupORM.owner_id == user.id)
    )
    group_id = query.scalar()

    new_thing = ThingORM(title=thing_title)
    session.add(new_thing)
    await session.commit()
    await session.refresh(new_thing)

    add_in_user_group = UserGroupORM(
        group_id=group_id,
        thing_id=new_thing.id,
        user_id=user.id)
    session.add(add_in_user_group)
    await session.commit()

    return UserGroupRead.from_orm(add_in_user_group).dict()


async def delete_thing_logic(
        title: str,
        session,
        user: UserORM):
    thing = await get_thing_by_title(title, user)
    group_id = await get_curr_user_group(user)

    del_in_usr_group = delete(UserGroupORM).where(and_(
        UserGroupORM.thing_id == thing.id,
        UserGroupORM.group_id == group_id
    ))

    parameters = await get_parameters_by_thing_title_logic(title, session, user)

    if parameters is not None:
        for parameter in parameters:
            await delete_parameter_logic(
                ParameterDelete(
                    thing_title=title,
                    key=parameter['key'],
                    value=parameter['value']
                ), session, user)

    res = delete(ThingORM).where(ThingORM.id == thing.id)

    await session.execute(del_in_usr_group)
    await session.execute(res)
    await session.commit()
    return {"status": "success"}
