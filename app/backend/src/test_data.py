import asyncio

from models import UserORM
from auth.manager import get_async_session_context
from auth.manager import create_user
from fastapi import Depends
from auth.base_config import current_user
from database import get_async_session
from parameters.manager import parameter_create_logic
from parameters.schemas import ParameterCreate
from things.manager import add_thing_logic


async def init_db(
        session=Depends(get_async_session),
        user: UserORM = Depends(current_user)
):
    async with get_async_session_context() as session:
        jonson = await create_user(email="jonson@gmail.com", username="jonson", password="9167249781269216")
        sabrina = await create_user(email="sabri@gmail.com", username="sabrina746", password="asasd32321")
        el_macho = await create_user(email="no_name@example.com", username="el_macho", password="91269asft8")
        admin = await create_user(email="admin", username="admin", password="admin")

        thing_titles = ["Калибраторы температуры КТ-1",
                        "Вольтметр амплитудный ВА-3.1",
                        "Термостат Термотест-300",
                        "Портативный pH-метр МАРК-903",
                        "Криостат Термотест-100",
                        "Измеритель-калибратор "
                        "сопротивлений прецизионный ИКС-1"]
        parameters = [
            ParameterCreate(key="image", value="static/image/admin/1.jpg"),
            ParameterCreate(key="image", value="static/image/admin/2.jpg"),
            ParameterCreate(key="image", value="static/image/admin/3.jpg"),
            ParameterCreate(key="image", value="static/image/admin/4.jpg"),
            ParameterCreate(key="image", value="static/image/admin/5.jpg"),
            ParameterCreate(key="image", value="static/image/admin/6.jpg"),
        ]
        for title, parameter in zip(thing_titles, parameters):
            await add_thing_logic(title, session, admin)
            await parameter_create_logic(title, parameter, session, admin)


if __name__ == "__main__":
    asyncio.run(init_db())
