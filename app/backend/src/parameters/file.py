import urllib.parse
from pathlib import Path

from fastapi import Depends, HTTPException, UploadFile, File
from fastapi.responses import FileResponse
from sqlalchemy.ext.asyncio import AsyncSession
from fastapi import APIRouter
from auth.base_config import current_user
from database import get_async_session
from models import UserORM
import urllib.parse
from .schemas import ParameterCreate
from .manager import parameter_create_logic


file_router = APIRouter(
    prefix="/file",
    tags=["Files"],
)


@file_router.get("/get_file/{file_path:path}")
async def get_any_file(file_path: str):
    decoded_path = urllib.parse.unquote(file_path)

    file = Path(decoded_path)

    if not file.exists() or not file.is_file():
        raise HTTPException(status_code=404, detail="File not found")

    return FileResponse(path=decoded_path, filename=file.name)


@file_router.post("/upload_file/{thing_title}")
async def upload_img(
        thing_title: str,
        img: UploadFile = File(...),
        user: UserORM = Depends(current_user),
        session: AsyncSession = Depends(get_async_session)
):
    upload_folder = Path(f"./static/image/{user.username}")
    upload_folder.mkdir(parents=True, exist_ok=True)

    document_path = upload_folder / img.filename
    with open(document_path, "wb") as file:
        content = await img.read()
        file.write(content)

    await parameter_create_logic(
        user=user,
        parameter=ParameterCreate(key="image", value=str(document_path)),
        thing_title=thing_title,
        session=session
    )

    return {"message": "Document uploaded successfully", "file_path": str(document_path)}
