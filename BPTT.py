import numpy as np


def dSigmoid(z): return z - z**2
def dTanh(t): return 1-t**2

gate = {
            "forget_gate": 0.6,
            "input_gate": 0.7,
            "output_gate": 0.5,
            "candidate_gate": 0.2,
            "cell_state": 0.15,
            "hidden_state": 0.075,
            "dc_next": 0,
            "dh_next": 0,
            "c_prev": 0.1,
            "h_prev": 0.05,
            "u_f": 0.3,
            "u_i": 0.2,
            "u_candidate": 0.4,
            "u_o": 0.1
}

def backwardPass(gate, target, x):
    y_t = target
    dc_next = 0
    dh_next = 0
    dL_dh = gate['hidden_state'] - y_t + gate['dh_next']
    dL_dc = dL_dh * gate['output_gate'] * (1 - np.tanh(gate['cell_state'])**2) + gate['dc_next']
    dL_df = dL_dc * gate['c_prev']
    dL_di = dL_dc * gate['candidate_gate']
    dL_do = dL_dh * np.tanh(gate['cell_state'])

    dL_dc_candidate = dL_dc * gate['input_gate']

    dz_f = dL_df * dSigmoid(gate['forget_gate'])
    dz_i = dL_di * dSigmoid(gate['input_gate'])
    dz_o = dL_do * dSigmoid(gate['output_gate'])
    dz_cand = dL_dc_candidate * (dTanh(gate['candidate_gate']))


    dh_next = (dz_f * gate['u_f']) + (dz_i * gate['u_i']) + (dz_cand * gate['u_candidate']) + (dz_o * gate['u_o'])
    
    print("dh_next:", dh_next)
    gate['dh_next'] = dh_next

    gradients_steps = {
        'W_f': dz_f*x, 'U_f': dz_f*gate['h_prev'], 'B_f': dz_f,
        'W_i': dz_i*x, 'U_i': dz_i*gate['h_prev'], 'B_i': dz_i,
        'W_cand': dz_cand*x, 'U_cand': dz_cand*gate['h_prev'], 'B_cand': dz_cand,
        'W_o': dz_o*x, 'U_o': dz_o*gate['h_prev'], 'B_O': dz_o,
    }

    return gradients_steps

backwardPass(gate, 0.1, 0.3)
