import sqlite3
import random

experiment_id = 1
# Connect to DB
conn = sqlite3.connect(r"C:\Users\RYZEN 5 3400G\Desktop\Project\HeartPulse2\heart_data.db")
cur = conn.cursor()

x = [0.8, -0.3, 0.5, 1.2, -0.6]



for timestep in range(len(x)):
    cur.execute("""
    insert into lstm_input (experiment_id, layer_name, timestep, input_value)
    values(?,?,?,?)
    """, (experiment_id, 'LSTM1', timestep+1, x[timestep]))

    conn.commit()

conn.close()