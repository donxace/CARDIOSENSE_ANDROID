import numpy as np
import sqlite3
import os
import time

db_path=r"C:\Users\RYZEN 5 3400G\Desktop\Project\HeartPulse2\heart_data.db"
conn = sqlite3.connect(db_path)
cursor = conn.cursor()

def fetch_column(table, column, exp_id, layer_name, timestep, weight_name=None):
    if weight_name is None:
        cursor.execute(f"""
            SELECT {column}
            FROM {table}
            WHERE experiment_id = ? AND layer_name = ? AND timestep = ?
        """, (exp_id, layer_name, timestep))
    else:
        cursor.execute(f"""
            SELECT {column}
            FROM {table}
            WHERE experiment_id = ? AND layer_name = ? AND weight_name = ? AND timestep = ?
        """, (exp_id, layer_name, weight_name, timestep))

    rows = cursor.fetchall()
    return np.array([r[0] for r in rows])

def fetch_column_heart

def standard_dev(rr_interval)

def sigmoid(x):
    sigm = 1/(1+np.exp(-x))

    return sigm

experiment_id = 1
layer_name = 'LSTM1'
h_tPrev = np.array([0.0]) 
c_tPrev = np.array([0.0])
0
timestep_start = 1
timestep_end = 5

for i in range(timestep_start, timestep_end+1):
    timestep = i
    x_t = fetch_column('lstm_input', 'input_value', experiment_id, layer_name, timestep)
    
    ##forget gate variables
    w_f= fetch_column('lstm_weights', 'value', experiment_id, layer_name, timestep, 'w_f')
    u_f= fetch_column('lstm_weights', 'value', experiment_id, layer_name, timestep, 'u_f')
    b_f = fetch_column('lstm_weights', 'value', experiment_id, layer_name, timestep, 'b_f')

    

    ##input gate variables
    w_i = fetch_column('lstm_weights', 'value', experiment_id, layer_name, timestep, 'w_i')
    u_i = fetch_column('lstm_weights', 'value', experiment_id, layer_name, timestep, 'u_i')
    b_i = fetch_column('lstm_weights', 'value', experiment_id, layer_name, timestep, 'b_i')

    

    ##output gate variables
    w_o = fetch_column('lstm_weights', 'value', experiment_id, layer_name, timestep, 'w_o')
    u_o = fetch_column('lstm_weights', 'value', experiment_id, layer_name, timestep, 'u_o')
    b_o = fetch_column('lstm_weights', 'value', experiment_id, layer_name, timestep, 'b_o')

    

    ##candidate gate variables
    w_ca = fetch_column('lstm_weights', 'value', experiment_id, layer_name, timestep, 'w_ca')
    u_ca = fetch_column('lstm_weights', 'value', experiment_id, layer_name, timestep, 'u_ca')
    b_ca = fetch_column('lstm_weights', 'value', experiment_id, layer_name, timestep, 'b_ca')

    

    def forgetGate(x_t, h_tPrev, w_f, u_f, b_f):
        forgetG = sigmoid(np.dot(x_t, w_f) + np.dot(u_f, h_tPrev) + b_f)

        return forgetG

    f_t = forgetGate(x_t, h_tPrev, w_f, u_f, b_f)

    def inputGate(x_t, h_tPrev, w_i, u_i, b_i):
        inputG = sigmoid(np.dot(x_t, w_i) + np.dot(u_i, h_tPrev) + b_i)

        return inputG

    i_t = inputGate(x_t, h_tPrev, w_i, u_i, b_i)

    def outputGate(x_t, h_tPrev, w_o, u_o, b_o):
        outputG = sigmoid(np.dot(x_t, w_o) + np.dot(u_o, h_tPrev) + b_o)

        return outputG

    o_t = outputGate(x_t, h_tPrev, w_o, u_o, b_o)

    def candidateState(x_t, h_tPrev, w_ca, u_ca, b_ca):
        candidate = np.tanh(np.dot(x_t, w_ca) + np.dot(u_ca, h_tPrev) + b_ca)

        return candidate

    ca_t = candidateState(x_t, h_tPrev, w_ca, u_ca, b_ca)

    def cellState(f_t, c_tPrev, i_t, ca_t):
        cell = np.dot(f_t, c_tPrev) + np.dot(i_t, ca_t)

        return cell

    c_t = cellState(f_t, c_tPrev, i_t, ca_t)

    
    def hiddenState(o_t, c_t):
        hidden = np.array(o_t * np.tanh(c_t))

        return hidden

    h_t = hiddenState(o_t, c_t)

    print("-----------------------------------")
    print(f"            TIMESTEP = {timestep}           ")
    print("-----------------------------------")

    print("Forget Gate: ", f_t)
    print("Input Gate: ", i_t)
    print("Output Gate: ", o_t)
    print("Candidate Gate: ", ca_t)
    print("Cell State: ", c_t)
    print("Hidden State: ", h_t)
    print("x_t: ", x_t)
    print("h_tPrev: ", h_tPrev)
    print("c_tPrev: ", c_tPrev)

    print("-----------------------------------")
    print(" FORWARD PASS (WEIGHTS AND BIASES) ")
    print("-----------------------------------")
    print("x_t: ", x_t)
    print("w_f: ", w_f)
    print("u_f: ", u_f)
    print("b_f: ", b_f)
    print("w_i: ", w_i)
    print("u_i: ", u_i)
    print("b_i: ", b_i)
    print("w_o: ", w_o)
    print("u_o: ", u_o)
    print("b_o: ", b_o)
    print("w_ca: ", w_o)
    print("u_ca: ", u_ca)
    print("b_ca: ", b_ca)
    print("u_f: ", u_f)

    time.sleep(0.5)

    h_tPrev = h_t
    c_tPrev = c_t
 
    gates = {
        "forget_gate": f_t,
        "input_gate": i_t,
        "output_gate": o_t,
        "candidate_gate": ca_t,
        "cell_state": c_t,
        "hidden_state": h_t
    }

    for table_name, gate_value in gates.items():
        cursor.execute(f"""
            insert into {table_name} (experiment_id, layer_name, timestep, value)
            values (?,?,?,?)
        """, (experiment_id, layer_name, timestep, gate_value.item()))

    conn.commit()