from pathlib import Path

import qrcode
from fastapi import Depends, HTTPException
from fastapi.responses import FileResponse
from sqlalchemy.ext.asyncio import AsyncSession
from fastapi import APIRouter
from auth.base_config import current_user
from database import get_async_session
from models import UserORM
from .manager import get_parameters_by_thing_title_logic, parameter_create_logic
from .schemas import ParameterCreate

qr_router = APIRouter(
    prefix="/qr",
    tags=["QR"],
)


class QrCodeNotFound(HTTPException):
    def __init__(self):
        super().__init__(status_code=404, detail="QR code not found")


@qr_router.post("/create_qr_code/{thing_title}")
async def create_qr_code(
        thing_title: str,
        user: UserORM = Depends(current_user),
        session=Depends(get_async_session)
):
    upload_folder_qr = Path(f"./static/qr/{user.username}")
    upload_folder_qr.mkdir(parents=True, exist_ok=True)

    parameters = await get_parameters_by_thing_title_logic(thing_title, session, user)
    if not parameters:
        raise HTTPException(status_code=404, detail="Don't know this thing")

    qr = qrcode.QRCode(
        version=1,
        error_correction=qrcode.constants.ERROR_CORRECT_L,
        box_size=10,
        border=4,
    )
    qr.add_data(thing_title)
    qr.make(fit=True)
    img = qr.make_image(fill='black', back_color='white')

    filename = f"{thing_title}_qr.png"
    file_path = upload_folder_qr / filename
    img.save(file_path)

    await parameter_create_logic(
        user=user,
        parameter=ParameterCreate(key="qr", value=str(file_path)),
        thing_title=thing_title,
        session=session
    )

    return {"message": "QR code generated successfully", "file_path": str(file_path)}


@qr_router.get("/get_qr_code/{thing_title}")
async def get_qr_code(
        thing_title: str,
        user: UserORM = Depends(current_user),
        session: AsyncSession = Depends(get_async_session)
):
    parameters = await get_parameters_by_thing_title_logic(thing_title, session, user)
    qr_parameter = next((param for param in parameters if param['key'] == 'qr'), None)
    if not qr_parameter:
        raise QrCodeNotFound()

    qr_code_path = Path(qr_parameter['value'])
    if not qr_code_path.exists():
        raise QrCodeNotFound()

    return FileResponse(path=qr_code_path, media_type='image/png', filename=qr_code_path.name)
