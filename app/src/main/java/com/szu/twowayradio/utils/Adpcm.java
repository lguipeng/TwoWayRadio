package com.szu.twowayradio.utils;


import com.szu.twowayradio.domains.AdpcmState;

public class Adpcm {

    private static  int indexTable[] =new int[]
    {
        -1, -1, -1, -1, 2, 4, 6, 8,
        -1, -1, -1, -1, 2, 4, 6, 8,
    };
	   
	   
    private static  int stepsizeTable[] =new int[]
    {
        7, 8, 9, 10, 11, 12, 13, 14, 16, 17,
        19, 21, 23, 25, 28, 31, 34, 37, 41, 45,
        50, 55, 60, 66, 73, 80, 88, 97, 107, 118,
        130, 143, 157, 173, 190, 209, 230, 253, 279, 307,
        337, 371, 408, 449, 494, 544, 598, 658, 724, 796,
        876, 963, 1060, 1166, 1282, 1411, 1552, 1707, 1878, 2066,
        2272, 2499, 2749, 3024, 3327, 3660, 4026, 4428, 4871, 5358,
        5894, 6484, 7132, 7845, 8630, 9493, 10442, 11487, 12635, 13899,
        15289, 16818, 18500, 20350, 22385, 24623, 27086, 29794, 32767
    };

    public static char adpcmCoder (short[] indata, byte[] outdata, int len, AdpcmState state)
    {

        short[]  inp;		/* Input buffer pointer */
        byte[]  outp;		/* Output buffer pointer */
        int val;			/* Current input sample value */
        int sign;			/* Current adpcm sign bit */
        int delta;			/* Current adpcm output value */
        int diff;			/* Difference between val and valprev */
        int step;			/* Stepsize */
        int valpred;		/* Predicted output value */
        int vpdiff;			/* Current change to valpred */
        int index;			/* Current step change index */
        int outputbuffer = 0;	/* place to keep previous 4-bit value */
        boolean bufferstep;		/* toggle between outputbuffer/output */

        char once = 0;
        char rc = 0;

        outp = outdata;
        inp = indata;

        valpred = state.getValprev();
        index = state.getIndex();
        step = stepsizeTable[index];

        bufferstep = true;

        int i=0;
        int j=0;
        for ( ; len > 0 ; len-- ) {
          val = inp[i];

        /* Step 1 - compute difference with previous value */
        diff = val - valpred;
        sign = (diff < 0) ? 8 : 0;
        if ( sign!=0 ) diff = (-diff);

        /* Step 2 - Divide and clamp */
        /* Note:
        ** This code *approximately* computes:
        **    delta = diff*4/step;
        **    vpdiff = (delta+0.5)*step/4;
        ** but in shift step bits are dropped. The net result of this is
        ** that even if you have fast mul/div hardware you cannot put it to
        ** good use since the fixup would be too expensive.
        */
        delta = 0;
        vpdiff = (step >> 3);

        if ( diff >= step ) {
            delta = 4;
            diff -= step;
            vpdiff += step;
        }
        step >>= 1;
        if ( diff >= step  ) {
            delta |= 2;
            diff -= step;
            vpdiff += step;
        }
        step >>= 1;
        if ( diff >= step ) {
            delta |= 1;
            vpdiff += step;
        }

        /* Step 3 - Update previous value */
        if ( sign !=0)
          valpred -= vpdiff;
        else
          valpred += vpdiff;

        /* Step 4 - Clamp previous value to 16 bits */
        if ( valpred > 32767 )
          valpred = 32767;
        else if ( valpred < -32768 )
          valpred = -32768;

        /* Step 5 - Assemble value, update index and step values */
        delta |= sign;

        index += indexTable[delta];
        if ( index < 0 ) index = 0;
        if ( index > 88 ) index = 88;

        if (once == 0)
        {
            rc = (char)index;
            once = 1;
        }

        step = stepsizeTable[index];

        /* Step 6 - Output value */
        if ( bufferstep ) {
            outputbuffer = delta & 0x0f;
        } else {

            outp[j]= (byte) (((delta << 4) & 0xf0) | outputbuffer);
//				    System.out.printf("%x ",outp[j]);
            j++;
        }
          bufferstep = !bufferstep;

          i++;
        }

        /* Output last step, if needed */
        if ( !bufferstep )
            outp[i] = (byte) outputbuffer;

        state.setValprev((short)valpred);
        state.setIndex((byte)index);

        return rc;
    }


    public static int code(short[] input, byte[] output, int len, AdpcmState state)
    {
        return code(state, input, 0, len, output, 0);
    }

    public static int code(AdpcmState state, short[] input, int inp, int len, byte[] output, int outp) {
        int sign;
        int delta;
        int vpdiff;

        int valprev = state.getValprev();
        int index = state.getIndex();

        int step = stepsizeTable[index];
        int outputbuffer = 0;
        int bufferstep = 1;

        output[outp + 2] = (byte)index;
        output[outp + 3] = (byte)0;
        outp += 4;

        int count = len;
        while (--count >= 0) {

            delta = input[inp++] - valprev;
            sign = (delta < 0) ? 8 : 0;
            if ( 0 != sign ) delta = (-delta);

            int tmp = 0;
            vpdiff = step >> 3;
            if ( delta > step ) {
                tmp = 4;
                delta -= step;
                vpdiff += step;
            }
            step >>= 1;
            if ( delta > step  ) {
                tmp |= 2;
                delta -= step;
                vpdiff += step;
            }
            step >>= 1;
            if ( delta > step ) {
                tmp |= 1;
                vpdiff += step;
            }
            delta = tmp;

            if ( 0 != sign )
                valprev -= vpdiff;
            else
                valprev += vpdiff;

            if ( valprev > 32767 )
                valprev = 32767;
            else if ( valprev < -32768 )
                valprev = -32768;

            delta |= sign;

            index += indexTable[delta];
            if ( index < 0 ) index = 0;
            if ( index > 88 ) index = 88;
            step = stepsizeTable[index];

            if ( 0 != bufferstep ) {
                outputbuffer = (delta << 4) & 0xf0;
            } else {
                output[outp++] = (byte)((delta & 0x0f) | outputbuffer);
            }
            bufferstep = (0 == bufferstep) ? 1 : 0;
        }

        if ( 0 == bufferstep )
            output[outp++] = (byte)outputbuffer;

        state.setValprev((short)valprev);
        state.setIndex((byte)index);
        return (len / 2) + 4;
    }

	public static  void adpcmDecoder (byte[] indata, short[] outdata, int len, AdpcmState state)
	{
        byte[] inp;		/* Input buffer pointer */
        short[] outp;		/* output buffer pointer */
        int sign;			/* Current adpcm sign bit */
        int delta;			/* Current adpcm output value */
        int step;			/* Stepsize */
        int valpred;		/* Predicted value */
        int vpdiff;			/* Current change to valpred */
        int index;			/* Current step change index */
        int inputbuffer = 0;	/* place to keep next 4-bit value */
        boolean bufferstep;		/* toggle between inputbuffer/input */

        outp = outdata;
        inp = indata;

        valpred = state.getValprev();
        index = state.getIndex();
        step = stepsizeTable[index];

        bufferstep = false;

        int i=0;
        int j=0;
        for ( ; len > 0 ; len-- ) {

            /* Step 1 - get the delta value */
            if ( bufferstep ) {
                delta = (inputbuffer >> 4) & 0xf;
            } else {
                inputbuffer = inp[j];
                delta = inputbuffer & 0xf;
                j++;
            }
            bufferstep = !bufferstep;

            /* Step 2 - Find new index value (for later) */
            index += indexTable[delta];
            if ( index < 0 ) index = 0;
            if ( index > 88 ) index = 88;

            /* Step 3 - Separate sign and magnitude */
            sign = delta & 8;
            delta = delta & 7;

            /* Step 4 - Compute difference and new predicted value */
            /*
            ** Computes 'vpdiff = (delta+0.5)*step/4', but see comment
            ** in adpcm_coder.
            */
            vpdiff = step >> 3;
            if ( (delta & 4)!=0 ) vpdiff += step;
            if ( (delta & 2)!=0 ) vpdiff += step>>1;
            if ( (delta & 1)!=0 ) vpdiff += step>>2;

            if ( sign!=0 )
              valpred -= vpdiff;
            else
              valpred += vpdiff;

            /* Step 5 - clamp output value */
            if ( valpred > 32767 )
              valpred = 32767;
            else if ( valpred < -32768 )
              valpred = -32768;

            /* Step 6 - Update step value */
            step = stepsizeTable[index];

            /* Step 7 - Output value */
            outp[i] = (short) valpred;
//					System.out.printf("%x ",outp[i]);
            i++;
        }

        state.setValprev((short) valpred);
        state.setIndex((byte) index);
	}

    public static int decode(byte[] input, short[] output, int len, AdpcmState state)
    {
        return decode(state, input, 0, len, output, 0);
    }

    public static int decode(AdpcmState state, byte[] input, int inp, int len, short[] output, int outp) {
        int sign;
        int delta;
        int vpdiff;
        int valprev = state.getValprev();
        int index = state.getIndex();
        int inputbuffer = 0;
        int bufferstep = 0;

        if ( index < 0 ) index = 0;
        else if ( index > 88 ) index = 88;

        int step = stepsizeTable[index];

        inp += 4;

        len = (len - 4) * 2;

        int count = len;
        while(count-- > 0) {

            if ( 0 == bufferstep ) {
                inputbuffer = input[inp++];
                delta = (inputbuffer >> 4) & 0xf;
                bufferstep = 1;
            } else {
                delta = inputbuffer & 0xf;
                bufferstep = 0;
            }

            index += indexTable[delta];
            if ( index < 0 ) index = 0;
            else if ( index > 88 ) index = 88;

            sign = delta & 8;
            delta = delta & 7;

            vpdiff = step >> 1;
            if ( (delta & 4) == 4 ) vpdiff += (step << 2);
            if ( (delta & 2) == 2 ) vpdiff += (step << 1);
            if ( (delta & 1) == 1 ) vpdiff += step;
            vpdiff >>= 2;

            if ( 0 != sign )
                valprev -= vpdiff;
            else
                valprev += vpdiff;

            if ( valprev > 32767 )
                valprev = 32767;
            else if ( valprev < -32768 )
                valprev = -32768;

            step = stepsizeTable[index];
            output[outp++] = (short) valprev;
        }

        state.setValprev((short) valprev);
        state.setIndex((byte) index);
        return len;
    }
	   
}
