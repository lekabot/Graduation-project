import asyncio

from models import ThingORM, ParameterORM, ThingParameterORM, GroupORM, UserGroupORM
from auth.manager import get_async_session_context
from auth.manager import create_user


async def init_db():
    async with get_async_session_context() as session:
        thing1 = ThingORM(title="Thing 1")
        thing2 = ThingORM(title="Thing 2")
        thing3 = ThingORM(title="Thing 3")

        param1 = ParameterORM(key="param1", value="value1")
        param2 = ParameterORM(key="param2", value="value2")
        param3 = ParameterORM(key="param3", value="value3")

        session.add_all([thing1, thing2, thing3, param1, param2, param3])
        await session.commit()

        tp1 = ThingParameterORM(thing_id=thing1.id, parameter_id=param1.id)
        tp2 = ThingParameterORM(thing_id=thing1.id, parameter_id=param2.id)
        tp3 = ThingParameterORM(thing_id=thing2.id, parameter_id=param2.id)
        tp4 = ThingParameterORM(thing_id=thing2.id, parameter_id=param3.id)
        tp5 = ThingParameterORM(thing_id=thing3.id, parameter_id=param1.id)

        session.add_all([tp1, tp2, tp3, tp4, tp5])
        await session.commit()

        group1 = GroupORM(title="group1", url="124124124")
        group2 = GroupORM(title="group2", url="12412312532432")
        group3 = GroupORM(title="group3", url="1252516423")

        session.add_all([group1, group2, group3])
        await session.commit()

        user1 = await create_user(email="345@example.com", username="345", password="1")
        user2 = await create_user(email="346@example.com", username="346", password="1")
        user3 = await create_user(email="347@example.com", username="347", password="1")

        ug1 = UserGroupORM(group_id=group1.id, thing_id=thing1.id, user_id=user1.id)
        ug2 = UserGroupORM(group_id=group2.id, thing_id=thing2.id, user_id=user2.id)
        ug3 = UserGroupORM(group_id=group3.id, thing_id=thing3.id, user_id=user3.id)

        session.add_all([ug1, ug2, ug3])
        await session.commit()


if __name__ == "__main__":
    asyncio.run(init_db())
