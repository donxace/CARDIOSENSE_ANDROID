import numpy as np

# Heart rate data
heart_rates = np.array([567, 568, 528, 608, 568, 839, 1078], dtype=np.float32)

# Sequence length
N = 5

# Create sequences + labels
sequences = []
labels = []
for i in range(len(heart_rates) - N):
    seq = heart_rates[i:i+N]
    label = heart_rates[i+N]
    # Sequence-wise normalization
    mean = seq.mean()
    std = seq.std() if seq.std() > 0 else 1
    norm_seq = (seq - mean) / std
    sequences.append(norm_seq)
    labels.append(label)

sequences = np.array(sequences)  # shape (2,5)
labels = np.array(labels)        # shape (2,)

print("Normalized Sequences:")
print(sequences)
print("Next heart rate labels:")
print(labels)