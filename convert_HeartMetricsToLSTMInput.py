import sqlite3
import numpy as np

# Connect to database
conn = sqlite3.connect(r"C:\Users\RYZEN 5 3400G\Desktop\Project\HeartPulse2\heart_data.db")
cursor = conn.cursor()

def run_heartMetrics_lstmInput():
    # 2️⃣ Fetch heart rate values from heart_metrics
    cursor.execute("SELECT Heart_Rate FROM heart_metrics")
    rows = cursor.fetchall()
    heart_rates = np.array([r[0] for r in rows], dtype=float)

    # 3️⃣ Define sequence length
    sequence_length = 5
    experiment_id = 1
    layer_name = "LSTM1"

    # 4️⃣ Generate sequences and sequence-wise normalize
    sequences = []
    for i in range(len(heart_rates) - sequence_length + 1):
        seq = heart_rates[i:i+sequence_length]
        mean = seq.mean()
        std = seq.std() if seq.std() > 0 else 1.0
        norm_seq = (seq - mean) / std
        sequences.append(norm_seq)

    # 5️⃣ Insert each value of each sequence into lstm_input with timestep starting at 1
    for seq_idx, seq in enumerate(sequences, start=1):
        for t, value in enumerate(seq, start=1):  # timestep starts at 1
            cursor.execute("""
                INSERT INTO lstm_input (experiment_id, layer_name, sequence, timestep, input_value)
                VALUES (?, ?, ?, ?, ?)
            """, (experiment_id, layer_name, seq_idx, t, float(value)))

    conn.commit()

    # Optional: check the table
    cursor.execute("SELECT * FROM lstm_input")
    for row in cursor.fetchall():
        print(row)
