from typing import Optional, List
from sqlalchemy import select, delete, update, join, and_
from fastapi import APIRouter, Depends
from models import ThingORM, ParameterORM, ThingParameterORM
from database import get_async_session
from .schemas import ParameterRead, ParameterUpdate, ParameterCreate, ParameterDelete

router_parameter = APIRouter(
    prefix="/parameter",
    tags=["Parameter"],
)


# None tested
# @router_parameter.get("/get_parameters_by_thing_name/{thing_name}")
# async def parameters_get_by_thing_name(
#         thing_name: str,
#         session=Depends(get_async_session)
# ) -> Optional[List[ParameterRead]]:
#     try:
#         async with session:
#             thing = await session.execute(select(ThingORM).where(ThingORM.title == thing_name))
#             thing = thing.scalars().first()
#             if thing is None:
#                 return None
#             query = select(ParameterORM).join(ThingParameterORM).where(ThingParameterORM.thing_id == thing.id)
#             result = await session.execute(query)
#             parameters = result.scalars().all()
#             if parameters:
#                 return [ParameterRead.from_orm(parameter) for parameter in parameters]
#             return None
#     except Exception as e:
#         print(f"An error occurred: {e}")
#         return None


# @router_parameter.put("/update/{parameter_id}")
# async def parameter_update(
#         parameter_id: int,
#         parameter: ParameterUpdate,
#         session=Depends(get_async_session)):
#     try:
#         async with session:
#             stmt = update(ParameterORM).where(
#                 ParameterORM.id == parameter_id
#             ).values(
#                 key=parameter.key,
#                 value=parameter.value
#             )
#         await session.execute(stmt)
#     except Exception as e:
#         print(f"An error occurred: {e}")
#         return None


# @router_parameter.delete("/delete/{parameter_id}")
# async def parameter_delete_by_id(
#         parameter_id: str,
#         session=Depends(get_async_session)):
#     try:
#         async with session:
#             stmt = delete(ParameterORM).where(ParameterORM.id == parameter_id)
#             n_stmt = delete(ThingParameterORM).where(ThingParameterORM.parameter_id == parameter_id)
#             await session.execute(stmt, n_stmt)
#             await session.commit()
#             return {"status": "success"}
#     except Exception as e:
#         print(f"An error occurred: {e}")
#         return None


# @router_parameter.delete("/delete/")
# async def delete(
#         par_del: ParameterDelete,
#         session=Depends(get_async_session)):
#     try:
#         async with session:
#             stmt = (
#                 select(ThingParameterORM.parameter_id).
#                 join(ParameterORM, ParameterORM.id == ThingParameterORM.parameter_id).
#                 join(ThingORM, ThingORM.id == ThingParameterORM.thing_id).
#                 where(
#                     and_(
#                         ThingORM.title == par_del.thing_title,
#                         ParameterORM.key == par_del.key
#                     )
#                 )
#             )
#             result = await session.execute(stmt)
#             parameter_id = result.scalar_one()
#
#             del_in_thing_param = delete(ThingParameterORM).where(
#                 ThingParameterORM.parameter_id == parameter_id
#             )
#             stmt = delete(ParameterORM).where(ParameterORM.id == parameter_id)
#
#             await session.execute(stmt)
#             await session.execute(del_in_thing_param)
#             await session.commit()
#             return {"status": "success"}
#     except Exception as e:
#         print(f"An error occurred: {e}")
#         return None
#
#
# @router_parameter.post("/parameter_create/{thing_title}")
# async def parameter_create(
#         thing_title: str,
#         parameter: ParameterCreate,
#         session=Depends(get_async_session)
# ):
#     try:
#         async with session:
#             new_param = ParameterORM(**parameter.dict())
#             session.add(new_param)
#             await session.commit()
#
#             result = await session.execute(
#                 select(ThingORM.id).where(ThingORM.title == thing_title)
#             )
#             thing_id = result.scalar_one_or_none()
#             if thing_id is None:
#                 raise ValueError(f"Thing with title '{thing_title}' not found")
#
#             new_thing_param = ThingParameterORM(thing_id=thing_id, parameter_id=new_param.id)
#             session.add(new_thing_param)
#             await session.commit()
#
#             return {"status": "success"}
#
#     except Exception as e:
#         print(f"An error occurred: {e}")
#         return None
