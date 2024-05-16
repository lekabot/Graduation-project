import os
import sys

import pytest

sys.path.append(os.path.join(sys.path[0], 'src'))

from src.main import app
from httpx import AsyncClient


@pytest.fixture
def anyio_backend():
    return 'asyncio'


@pytest.mark.anyio
async def test_authentication():
    async with AsyncClient(app=app, base_url="http://127.0.0.1") as ac:
        response = await ac.post("/auth/login", data={"username": "1", "password": "1"})
    assert response.status_code == 204


@pytest.mark.anyio
async def test_registration():
    async with AsyncClient(app=app, base_url="http://127.0.0.1") as ac:
        response = await ac.post("/auth/register",
                                 json={
                                     "email": "string1",
                                     "password": "string",
                                     "is_active": True,
                                     "is_superuser": False,
                                     "is_verified": False,
                                     "username": "string1"
                                 })
        username = "string1"
        await ac.delete(f"/user/delete_by_username/{username}")
    assert response.status_code == 201
    assert "id" in response.json()


async def test_delete_by_username_user_found():
    async with AsyncClient(app=app, base_url="http://127.0.0.1") as ac:
        await ac.post("/auth/register",
                      json={
                          "email": "string1",
                          "password": "string",
                          "is_active": True,
                          "is_superuser": False,
                          "is_verified": False,
                          "username": "string1"
                      })
        username = "string1"
        response = await ac.delete(f"/user/delete_by_username/{username}")
    assert response.status_code == 204


async def test_delete_by_username_link_user_not_found():
    username = "333199y39h932gf9073g27f9g3207gf3280fg823gf80327gf832f"
    async with AsyncClient(app=app, base_url="http://127.0.0.1") as ac:
        response = await ac.delete(f"/user/delete_by_username/{username}")
    assert response.status_code == 404


async def test_get_by_id():
    user_id = 1
    async with AsyncClient(app=app, base_url="http://127.0.0.1") as ac:
        response = await ac.get(f"/user/get_by_id/{user_id}")
    assert response.status_code == 200


async def test_get_by_username():
    username = 1
    async with AsyncClient(app=app, base_url="http://127.0.0.1") as ac:
        response = await ac.get(f"/user/get_by_id/{username}")
    assert response.status_code == 200
