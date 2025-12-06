import matplotlib.pyplot as plt
from matplotlib.animation import FuncAnimation
import numpy as np

def animate_rr(actual_rr, predicted_rr):
    """
    Animates actual and predicted RR intervals even if they have different lengths.
    """

    actual_rr = np.array(actual_rr)
    predicted_rr = np.array(predicted_rr)

    len_actual = len(actual_rr)
    len_pred = len(predicted_rr)

    max_len = max(len_actual, len_pred)

    fig, ax = plt.subplots(figsize=(10, 5))

    # Axis limits: allow biggest dataset to fit
    ax.set_xlim(0, max_len - 1)
    ax.set_ylim(
        min(actual_rr.min(), predicted_rr.min()) - 10,
        max(actual_rr.max(), predicted_rr.max()) + 10
    )

    ax.set_xlabel("Sample Index")
    ax.set_ylabel("RR Interval (ms)")
    ax.set_title("Animated Actual vs Predicted RR Intervals")
    ax.grid(True)

    # Empty lines
    actual_line, = ax.plot([], [], label="Actual RR", linewidth=1)
    pred_line,   = ax.plot([], [], label="Predicted RR", linewidth=1)
    ax.legend()

    def update(frame):
        # actual
        if frame < len_actual:
            actual_line.set_data(
                np.arange(frame + 1),
                actual_rr[:frame + 1]
            )

        # predicted
        if frame < len_pred:
            pred_line.set_data(
                np.arange(frame + 1),
                predicted_rr[:frame + 1]
            )

        return actual_line, pred_line

    ani = FuncAnimation(
        fig,
        update,
        frames=max_len,   # animate until longest is done
        interval=30,
        blit=False,
        repeat=False
    )

    plt.show()
    return ani
