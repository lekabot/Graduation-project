from fastapi import UploadFile, File
from pathlib import Path
from fastapi import Depends, HTTPException
from fastapi.responses import FileResponse
from sqlalchemy.ext.asyncio import AsyncSession
from fastapi import APIRouter
from auth.base_config import current_user
from database import get_async_session
from models import UserORM
from .manager import get_parameters_by_thing_title_logic, parameter_create_logic
from .schemas import ParameterCreate

docs_router = APIRouter(
    prefix="/documents",
    tags=["Documents"],
)


class DocumentNotFound(HTTPException):
    def __init__(self):
        super().__init__(status_code=404, detail="Document not found")


@docs_router.post("/upload_document/{thing_title}")
async def upload_document(
        thing_title: str,
        document: UploadFile = File(...),
        user: UserORM = Depends(current_user),
        session: AsyncSession = Depends(get_async_session)
):
    upload_folder = Path(f"./static/documents/{user.username}")
    upload_folder.mkdir(parents=True, exist_ok=True)

    document_path = upload_folder / document.filename
    with open(document_path, "wb") as file:
        content = await document.read()
        file.write(content)

    await parameter_create_logic(
        user=user,
        parameter=ParameterCreate(key="document", value=str(document_path)),
        thing_title=thing_title,
        session=session
    )

    return {"message": "Document uploaded successfully", "file_path": str(document_path)}


@docs_router.get("/get_document/{thing_title}")
async def get_document(
        thing_title: str,
        user: UserORM = Depends(current_user),
        session: AsyncSession = Depends(get_async_session)
):
    parameters = await get_parameters_by_thing_title_logic(thing_title, session, user)
    document_parameter = next((param for param in parameters if param['key'] == 'document'), None)
    if not document_parameter:
        raise DocumentNotFound()

    document_path = Path(document_parameter['value'])
    if not document_path.exists():
        raise DocumentNotFound()

    return FileResponse(path=document_path, media_type='application/octet-stream', filename=document_path.name)
