from fastapi import APIRouter, Response, HTTPException
from .manager import delete_by_username, get_by_id, get_by_username

user_router = APIRouter(
    prefix="/user",
    tags=["User"]
)


@user_router.delete("/delete_by_username/{username}")
async def delete_by_username_link(username: str, response: Response):
    db_answer = await delete_by_username(username)
    if db_answer == 0:
        response.status_code = 204
    else:
        raise HTTPException(status_code=404, detail=f"User with username '{username}' not found")


@user_router.get("/get_by_id/{user_id}")
async def get_by_id_link(user_id: int):
    try:
        db_answer = await get_by_id(user_id)
        if db_answer is None:
            raise HTTPException(status_code=404, detail="User not found")
        return db_answer
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@user_router.get("/get_by_username/{username}")
async def get_by_username_link(username: str):
    try:
        db_answer = await get_by_username(username)
        if db_answer is None:
            raise HTTPException(status_code=404, detail=f"User with username '{username}' not found")
        return db_answer
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
