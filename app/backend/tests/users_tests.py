import os
import sys
import uuid

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


unique_username = str(uuid.uuid4())
unique_email = f"{unique_username}@example.com"


@pytest.mark.anyio
async def test_registration():
    async with AsyncClient(app=app, base_url="http://127.0.0.1") as ac:
        response = await ac.post("/auth/register",
                                 json={
                                     "email": unique_email,
                                     "password": "string",
                                     "is_active": True,
                                     "is_superuser": False,
                                     "is_verified": False,
                                     "username": unique_username
                                 })

        assert response.status_code == 201
        response_data = response.json()

        user_id = response_data["id"]
        group_create_response = await ac.get(f"/group/get_group_by_user_id/{user_id}")

        username = unique_username

        await ac.delete(f"/group/delete_by_username/{username}")
        await ac.delete(f"/user/delete_by_username/{username}")

        assert group_create_response.status_code == 200


async def test_delete_by_username_user_found():
    async with AsyncClient(app=app, base_url="http://127.0.0.1") as ac:
        await ac.post("/auth/register",
                      json={
                          "email": unique_email,
                          "password": "string",
                          "is_active": True,
                          "is_superuser": False,
                          "is_verified": False,
                          "username": unique_username
                      })
        username = unique_username
        response = await ac.delete(f"/user/delete_by_username/{username}")
    assert response.status_code == 204


async def test_delete_by_username_link_user_not_found():
    username = unique_username
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
