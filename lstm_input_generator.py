import sqlite3
import random

experiment_id = 1
# Connect to DB
conn = sqlite3.connect(r"C:\Users\RYZEN 5 3400G\Desktop\Project\HeartPulse2\heartbeat\heart_data.db")
cur = conn.cursor()

for timestep in range(100):
    x = random.randint(780,840)
    
    cur.execute("""
    insert into heart_metrics (rr_interval)
    values(?)
    """, (x,))

    conn.commit()

conn.close()