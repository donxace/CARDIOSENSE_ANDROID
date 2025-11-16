import numpy as np


def dSigmoid(z): return z - z**2
def dTanh(t): return 1-t**2

gate = {
    'x': -0.7,
    'f_t': 0.475,
    'i_t': 0.55,
    'c_candidate': -0.0997,
    'o_t': 0.476,
    'c_t': -0.1506,
    'h_t': -0.0712,
    'c_prev': -0.2,
    'h_prev': 0.1,
    'dc_next': 0
}

def backwardPass(gate, target):
    y_t = target
    dc_next = 0
    dL_dh = gate['h_t'] - y_t
    dL_dc = dL_dh * gate['o_t'] * (1 - np.tanh(gate['c_t'])**2) + gate['dc_next']
    dL_df = dL_dc * gate['c_prev']
    dL_di = dL_dc * gate['c_candidate']
    dL_do = dL_dh * np.tanh(gate['c_t'])

    dL_dc_candidate = dL_dc * gate['i_t']

    dz_f = dL_df * dSigmoid(gate['f_t'])
    dz_i = dL_di * dSigmoid(gate['i_t'])
    dz_o = dL_do * dSigmoid(gate['o_t'])
    dz_cand = dL_dc_candidate * (dTanh(gate['c_candidate']))

    gradients_steps = {
        'W_f': dz_f*gate['x'], 'U_f': dz_f*gate['h_prev'], 'B_f': dz_f,
        'W_i': dz_i*gate['x'], 'U_i': dz_i*gate['h_prev'], 'B_i': dz_i,
        'W_cand': dz_cand*gate['x'], 'U_cand': dz_cand*gate['h_prev'], 'B_cand': dz_cand,
        'W_o': dz_o*gate['x'], 'U_o': dz_o*gate['h_prev'], 'B_O': dz_o,
    }

    return gradients_steps

for k, v in backwardPass(gate, 0.4).items():
    print(f"{k} = {v}")
