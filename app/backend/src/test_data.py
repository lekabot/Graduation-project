import asyncio
from things.models import ThingORM, ParameterORM, ThingParameterORM
from auth.manager import get_async_session_context, get_user_manager_context
from auth import schemas


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

    async with get_user_manager_context() as user_manager:
        await user_manager.create(
            schemas.UserCreate(email="john@example.com", username="johndoe", password="password1"))
        await user_manager.create(
            schemas.UserCreate(email="jane@example.com", username="janedoe", password="password2"))
        await user_manager.create(
            schemas.UserCreate(email="admin@example.com", username="admin", password="password3"))

if __name__ == "__main__":
    asyncio.run(init_db())
