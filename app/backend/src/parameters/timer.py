# from fastapi import FastAPI, Depends, HTTPException
# from sqlalchemy.ext.asyncio import AsyncSession
# from sqlalchemy.future import select
# from sqlalchemy.orm import joinedload
# from database import get_db
# from models import NotificationORM, UserORM
# from .schemas import NotificationCreate
# from .scheduler import schedule_task, start_scheduler
# from datetime import datetime
# from auth.base_config import current_user
# from fastapi import APIRouter
#
# start_scheduler()
#
# timer_router = APIRouter(
#     prefix="/notifications",
#     tags=["notifications"],
# )
#
#
# @timer_router.post("/notifications/")
# async def create_notification(
#         notification: NotificationCreate,
#         user: UserORM = Depends(current_user),
#         db: AsyncSession = Depends(get_db)):
#     db_notification = NotificationORM(
#         user_id=user.id,
#         datetime=notification.datetime,
#     )
#     db.add(db_notification)
#     await db.commit()
#     await db.refresh(db_notification)
#
#     schedule_task(db_notification.id, db_notification.datetime)
#
#     return db_notification
