import matplotlib.pyplot as plt
import numpy as np
from scipy.interpolate import make_interp_spline   # <-- spline smoothing

def graph(rr_intervals, predict, max_points=2000, smooth=False, smooth_window=20):
    """
    Plots RR intervals and predictions together.
    Includes automatic downsampling + optional smoothing + smooth curves (spline).
    """

    rr_intervals = np.array(rr_intervals)
    predict = np.array(predict)

    # -----------------------------------------------------------
    # 1. OPTIONAL MOVING AVERAGE SMOOTHING
    # -----------------------------------------------------------
    if smooth and len(rr_intervals) > smooth_window:
        kernel = np.ones(smooth_window) / smooth_window
        rr_intervals = np.convolve(rr_intervals, kernel, mode='valid')

    # -----------------------------------------------------------
    # 2. AUTO-DOWNSAMPLE
    # -----------------------------------------------------------
    if len(rr_intervals) > max_points:
        step = len(rr_intervals) // max_points
        rr_intervals = rr_intervals[::step]

    # -----------------------------------------------------------
    # 3. X-axis for RR intervals
    # -----------------------------------------------------------
    x_rr = np.arange(len(rr_intervals))

    # -----------------------------------------------------------
    # 4. Prediction connection
    # -----------------------------------------------------------
    predict_with_connection = np.concatenate([[rr_intervals[-1]], predict])
    x_pred = np.arange(len(rr_intervals)-1, len(rr_intervals)-1 + len(predict_with_connection))

    # -----------------------------------------------------------
    # 5. SMOOTH CURVE (SPLINE INTERPOLATION)
    # -----------------------------------------------------------
    if smooth and len(rr_intervals) > 3:
        # RR
        spline_rr = make_interp_spline(x_rr, rr_intervals, k=3)
        x_rr_smooth = np.linspace(x_rr.min(), x_rr.max(), 500)
        rr_smooth = spline_rr(x_rr_smooth)

        # Prediction
        spline_pred = make_interp_spline(x_pred, predict_with_connection, k=3)
        x_pred_smooth = np.linspace(x_pred.min(), x_pred.max(), 500)
        pred_smooth = spline_pred(x_pred_smooth)
    else:
        x_rr_smooth, rr_smooth = x_rr, rr_intervals
        x_pred_smooth, pred_smooth = x_pred, predict_with_connection

    # -----------------------------------------------------------
    # 6. Plot
    # -----------------------------------------------------------
    plt.figure(figsize=(12, 6))

    plt.plot(x_rr_smooth, rr_smooth, label='RR Intervals')
    plt.plot(x_pred_smooth, pred_smooth, label='Prediction')

    plt.title("RR Interval and Prediction (Smoothed Curve)")
    plt.xlabel("Beat Number")
    plt.ylabel("RR Interval (ms)")
    plt.legend()
    plt.grid(True)
    plt.tight_layout()
    plt.show()
