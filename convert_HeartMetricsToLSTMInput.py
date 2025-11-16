import sqlite3
import numpy as np

# Connect to database
conn = sqlite3.connect(r"C:\Users\RYZEN 5 3400G\Desktop\Project\HeartPulse2\heartbeat\heart_data.db")
cursor = conn.cursor()

def run_heartMetrics_lstmInput():
    cursor.execute("delete from lstm_input")
    # 1️⃣ Fetch heart rate values from heart_metrics
    cursor.execute("SELECT rr_interval FROM heart_metrics")
    rows = cursor.fetchall()
    rr_interval = np.array([r[0] for r in rows], dtype=float)

    # 2️⃣ Define sequence length
    sequence_length = 5
    experiment_id = 1
    layer_name = "LSTM1"

    # 3️⃣ Generate sequences and sequence-wise normalize
    sequences = []
    sequence_stats = []  # store mean and std for each sequence
    for i in range(len(rr_interval) - sequence_length + 1):
        seq = rr_interval[i:i+sequence_length]
        mean = seq.mean()
        std = seq.std() if seq.std() > 0 else 1.0
        norm_seq = (seq - mean) / std
        sequences.append(norm_seq)
        sequence_stats.append((mean, std))

    # 4️⃣ Insert each value of each sequence into lstm_input with mean and std
    for seq_idx, (seq, stats) in enumerate(zip(sequences, sequence_stats), start=1):
        mean, std = stats
        for t, value in enumerate(seq, start=1):  # timestep starts at 1
            cursor.execute("""
                INSERT INTO lstm_input (experiment_id, layer_name, sequence, timestep, input_value, mean, standard_dev)
                VALUES (?, ?, ?, ?, ?, ?, ?)
            """, (experiment_id, layer_name, seq_idx, t, float(value), float(mean), float(std)))

    conn.commit()

    # Optional: check the table
    cursor.execute("SELECT * FROM lstm_input")
    for row in cursor.fetchall():
        print(row)

#run_heartMetrics_lstmInput()

