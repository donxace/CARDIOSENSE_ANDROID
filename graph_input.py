import matplotlib.pyplot as plt

def graph(rr_intervals, predict):
    """
    Plots RR intervals and predictions together, connecting them smoothly.
    """

    # X-axis for RR intervals
    x_rr = list(range(len(rr_intervals)))

    # Connect prediction to last RR interval
    predict_with_connection = [rr_intervals[-1]] + predict

    # X-axis for prediction: starts at last RR beat
    x_pred = list(range(len(rr_intervals)-1, len(rr_intervals)-1 + len(predict_with_connection)))

    # Plotting
    plt.figure(figsize=(12, 6))
    plt.plot(x_rr, rr_intervals, marker='o', linestyle='-', color='red', label='RR Intervals')
    plt.plot(x_pred, predict_with_connection, marker='o', linestyle='-', color='blue', label='Prediction')
    plt.title("RR Interval and Prediction over Sequence")
    plt.xlabel("Beat Number")
    plt.ylabel("RR Interval (ms)")
    plt.legend()
    plt.grid(True)
    plt.tight_layout()
    plt.show()


