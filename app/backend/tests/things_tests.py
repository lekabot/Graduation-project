import os
import sys

import httpx
import pytest
import pytest_asyncio

sys.path.append(os.path.join(sys.path[0], 'src'))

from src.main import app
from httpx import AsyncClient


@pytest.fixture
def anyio_backend():
    return 'asyncio'


@pytest_asyncio.fixture()
async def client():
    async with AsyncClient(app=app, base_url="http://127.0.0.1") as client:
        yield client


@pytest.mark.asyncio
async def test_add_thing(client: httpx.AsyncClient):
    thing_title = "NewThing"
    response = await client.post("/thing/add_thing", json={"title": thing_title})

    assert response.status_code == 200
    assert response.json() == {"status": "success"}
    await client.delete(f"/thing/delete_thing_by_name/{thing_title}")


@pytest.mark.asyncio
async def test_delete_thing(client: httpx.AsyncClient):
    thing_title = "NewThing"
    await client.post("/thing/add_thing", json={"title": thing_title})
    response = await client.delete(f"/thing/delete_thing_by_name/{thing_title}")
    assert response.status_code == 200
    assert response.json() == {"status": "success"}


@pytest.mark.asyncio
async def test_get_by_name_thing(client: httpx.AsyncClient):
    thing_title_1 = "Urmpz"
    thing_title_2 = "Urmpzt"

    await client.post("/thing/add_thing", json={"title": thing_title_1})
    await client.post("/thing/add_thing", json={"title": thing_title_2})

    thing_name = "mpz"
    response = await client.get(f"/thing/get_by_name/{thing_name}")
    assert response.status_code == 200

    data = response.json()

    assert data[0]["title"] == "Urmpz"
    assert data[1]["title"] == "Urmpzt"

    await client.delete(f"/thing/delete_thing_by_name/{thing_title_1}")
    await client.delete(f"/thing/delete_thing_by_name/{thing_title_2}")


@pytest.mark.asyncio
async def test_get_parameters_by_name(client: httpx.AsyncClient):
    thing_title_1 = ""
    await client.post("/thing/add_thing", json={"title": thing_title_1})
