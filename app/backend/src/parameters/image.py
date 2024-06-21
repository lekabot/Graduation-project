# from pathlib import Path
#
# import qrcode
# from fastapi import Depends, HTTPException
# from fastapi.responses import FileResponse
# from sqlalchemy.ext.asyncio import AsyncSession
# from fastapi import APIRouter
# from auth.base_config import current_user
# from database import get_async_session
# from models import UserORM
# from .manager import get_parameters_by_thing_title_logic, parameter_create_logic
# from .schemas import ParameterCreate
# import urllib.parse
#
# image_router = APIRouter(
#     prefix="/image",
#     tags=["Images"],
# )
#
#
# @image_router.post("/add_img/{img}")
# def add_img(
#         img: Image,
#         user: UserORM = Depends(current_user),
#         session=Depends(get_async_session)
# ):
#     upload_folder_qr = Path(f"./static/qr/{user.username}")
#     upload_folder_qr.mkdir(parents=True, exist_ok=True)
