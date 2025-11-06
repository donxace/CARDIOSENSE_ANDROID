import sqlite3

conn = sqlite3.connect("heart_data.db")
cursor = conn.cursor()

cursor.execute("""
CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    rr_interval REAL NOT NULL,
    forget_gate REAL,
    input_gate REAL,
    output_gate REAL,
    candidate_gate REAL,
    cell_state REAL,
    hidden_state REAL,
    forget_weight REAL,
    input_weight REAL,
    output_weight REAL,
    candidate_weight REAL,
    forget_bias REAL,
    input_bias REAL,
    output_bias REAL,
    candidate_bias REAL   
)          
""")

conn.commit()
conn.close()