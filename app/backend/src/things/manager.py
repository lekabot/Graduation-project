from database import get_async_session
from models import UserORM, GroupORM, ThingORM, UserGroupORM
from sqlalchemy import select, delete, and_

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
    return {"status": "success"}