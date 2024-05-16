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
async def test_create_parameter(client: httpx.AsyncClient):
    thing_id = 1
    data = \
        {
            "key": "stop",
            "value": "working"
        }
    response = await client.post(f"/parameter_create/{thing_id}", json=data)
    assert response.status_code == 200
    assert response.json() == {"status": "success"}

