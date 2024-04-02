from flask import Flask, request, jsonify
from models import User, session

app = Flask(__name__)

@app.route('/api/v1.0/login', methods=['POST'])
def login():
    username = request.json.get['username']
    password = request.json.get['password']

    user = session.query(User).filter_by(username=username, password=password).first()
    if user:
        return jsonify({'status': 'success'}), 200
    else:
        return jsonify({'status': 'error', 'message': 'Invalid username or password'}), 401