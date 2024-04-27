from flask import Flask, request, jsonify

from models import User
from models import session

app = Flask(__name__)


@app.route('/api/v1.0/login', methods=['POST'])
def login():
    data = request.json
    username = data.get('username')
    password = data.get('password')

    user = session.query(User).filter_by(username=username, password=password).first()
    if user:
        return jsonify({'status': 'success', 'message': 'success'}), 200
    else:
        return jsonify({'status': 'error', 'message': 'Invalid username or password'}), 401

@app.route('/check')
def check():
    return 'Hello, world!'