import os
import sys

from fastapi.testclient import TestClient

sys.path.append(os.path.join(sys.path[0], 'src'))

from src.main import app

client = TestClient(app)


def test_logout():
    response_login = client.post("/auth/login", json={"username": "1", "password": "1"})
    assert response_login.status_code >= 200
    response_logout = client.post("/auth/logout")
    assert response_logout.status_code >= 200


def test_authentication():
    response = client.post("/auth/login", json={"username": "1", "password": "1"})
    assert response.status_code >= 200


def test_registration():
    response = client.post("/auth/register",
                           json={
                               "email": "string",
                               "password": "string",
                               "is_active": True,
                               "is_superuser": False,
                               "is_verified": False,
                               "username": "string"
                           })

    assert response.status_code >= 200
    assert "id" in response.json()
