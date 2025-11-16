import numpy as np
import sqlite3
import os
import time

import arduino_heartlogger as ard
import convert_HeartMetricsToLSTMInput as hm_lstm
import graph_input
import BPTT as bptt

##ard.run_heart_logger()
hm_lstm.run_heartMetrics_lstmInput()

show_prediction = []
show_timestep = []
show_sequence = []

def normalized(mean, standard_dev, x):
    normal = x*standard_dev+mean

    return normal

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

def sigmoid(x):
    sigm = 1/(1+np.exp(-x))

    return sigm

experiment_id = 1
layer_name = 'LSTM1'

timestep_start = 1
timestep_end = 5

h_tPrev = np.array([0.0]) 
c_tPrev = np.array([0.0])

cursor.execute("select max(sequence) from lstm_input")
range_rr = cursor.fetchone()[0] + 1

print(range_rr)
 

for h in range(1, range_rr):
    for i in range(timestep_start, timestep_end+1):
        timestep = i
        
        cursor.execute("""
            SELECT input_value
            FROM lstm_input
            WHERE experiment_id = ? and timestep = ? and sequence = ?
        """, (1, timestep, h))

        print(h)
        print(i)
        rows = cursor.fetchone()
        x_t = rows[0]
        
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
        print(" FORWARD PASS (WEIGHTS AND BIASES) ")
        print(f"        SEQUENCE = {h}            ")
        print(f"        TIMESTEP = {timestep}     ")
        print("-----------------------------------")
        print("h_tPrev: ", h_tPrev)
        print("c_tPrev: ", c_tPrev)
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
        print("-----------------------------------")
        print("             Memory Block        ")
        print("-----------------------------------")
        print("Forget Gate: ", f_t)
        print("Input Gate: ", i_t)
        print("Output Gate: ", o_t)
        print("Candidate Gate: ", ca_t)
        print("Cell State: ", c_t)
        print("Hidden State: ", h_t)

        h_tPrev = h_t
        c_tPrev = c_t
    
        gate = {
            "forget_gate": f_t,
            "input_gate": i_t,
            "output_gate": o_t,
            "candidate_gate": ca_t,
            "cell_state": c_t,
            "hidden_state": h_t,
            "dc_next": 0,
            "dh_next": 0,
            "c_prev": c_tPrev,
            "h_prev": h_tPrev,
            "u_f": u_f,
            "u_i": u_i,
            "u_candidate": u_ca,
            "u_o": u_o
        }
        

        meow = bptt.backwardPass(gate, 0.6, x_t)

        print("DH NEXT AAAAAAAAAAAAA: ",gate['dh_next'])

        for k, v in meow.items():
            print(f"{k}: {v}")

        for table_name, gate_value in list(gate.items())[:6]:
            cursor.execute(f"""
                insert into {table_name} (experiment_id, layer_name, timestep, value)
                values (?,?,?,?)
            """, (experiment_id, layer_name, timestep, gate_value.item()))

        conn.commit()

        cursor.execute("""
                SELECT mean
                FROM lstm_input
                WHERE timestep = ? AND sequence = ?
            """, (i, h))

        mean_r = cursor.fetchone()
        mean = mean_r[0]

        print("mean: ", mean)

        cursor.execute("""
                SELECT standard_dev
                FROM lstm_input
                WHERE timestep = ? AND sequence = ?
            """, (i, h))

        standard_dev_r = cursor.fetchone()
        
        standard_dev = standard_dev_r[0]

        print("standard_dev", standard_dev)

        prediction = normalized(mean, standard_dev, float(h_t[0]))
        target = normalized(mean, standard_dev, float(x_t))

        cursor.execute("""
            insert into lstm_prediction (sequence, timestep, predicted_value, target_value, mean, standard_dev)
            values (?,?,?,?,?,?)
        """, (h, i, prediction, target, float(mean), float(standard_dev)))

    show_prediction.append(prediction)
    print("-----------------------------------")
    print(f"PREDICTED VALUE (normalized) = {h_t} MS           ")
    print(f"PREDICTED VALUE (denormalized) = {prediction} MS           ")
    print(f"TARGET = {target}MS")
    print("-----------------------------------\n\n\n")

cursor.execute("select rr_interval from heart_metrics")
rows = cursor.fetchall()

rr = [row[0] for row in rows]

graph_input.graph(rr, show_prediction)


