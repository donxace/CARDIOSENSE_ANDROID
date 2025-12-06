import sqlite3
import random
# Connect to DB
conn = sqlite3.connect(r"C:\Users\HUAWEI\Desktop\HEARTBEAT\heart_data.db")
cur = conn.cursor()

experiment_id = 1
# Data to insert

gates = {
    "w_f": 0.4,
    "u_f": 0.3,
    "b_f": 0.0,
    "w_i": 0.5,
    "u_i": 0.2,
    "b_i": 0.0,
    "w_o": 0.7,
    "u_o": 0.1,
    "b_o": 0.0,
    "w_ca": 0.6,
    "u_ca": 0.4,
    "b_ca": 0.0
}

for timestep in range(1, 6):
    rows = [
        ##experiment_id, timestep, gate_name, weighty_type, weight_name, value, layer_name
        (experiment_id, timestep, "forget", "input",  "w_f", gates["w_f"], "LSTM1"),
        (experiment_id, timestep, "forget", "hidden", "u_f", gates["u_f"], "LSTM1"),
        (experiment_id, timestep, "forget", "bias",   "b_f", gates["b_f"], "LSTM1"),

        (experiment_id, timestep, "input", "input",  "w_i", gates["w_i"], "LSTM1"),
        (experiment_id, timestep, "input", "hidden", "u_i", gates["u_i"], "LSTM1"),
        (experiment_id, timestep, "input", "bias",   "b_i", gates["b_i"], "LSTM1"),

        (experiment_id, timestep, "output", "input",  "w_o", gates["w_o"], "LSTM1"),
        (experiment_id, timestep, "output", "hidden", "u_o", gates["u_o"], "LSTM1"),
        (experiment_id, timestep, "output", "bias",   "b_o", gates["b_o"], "LSTM1"),

        (experiment_id, timestep, "candidate", "input", "w_ca", gates["w_ca"], "LSTM1"),
        (experiment_id, timestep, "candidate", "hidden", "u_ca", gates["u_ca"], "LSTM1"),
        (experiment_id, timestep, "candidate", "bias",  "b_ca", gates["b_ca"], "LSTM1"),
    ]

    # Insert query
    cur.executemany("""
    INSERT INTO lstm_weights
    (experiment_id, timestep, gate_name, weight_type, weight_name, value, layer_name)
    VALUES (?, ?, ?, ?, ?, ?, ?)
    """, rows)

    conn.commit()
