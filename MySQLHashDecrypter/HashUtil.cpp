/* This program is public domain. Share and enjoy.
*  Please note I(TheShadow) did NOT make the original cracking code in this file.
*  I simply edited it to work with the JNI and interface with my Java program which functions more or less as a GUI to the original code
*  
*  Credits for the cracking code go to the original creator, who I do not know
*  Credits for the code making it interface to my Java program do go to me though
*/

#include "stdafx.h"
#include "HashUtil.h"

typedef unsigned long u32;

/* Allowable characters in password; 33-126 is printable ascii */
#define MIN_CHAR 33
#define MAX_CHAR 126

/* Maximum length of password */
#define MAX_LEN 12

#define MASK 0x7fffffffL

int* pass = new int[13];
bool isComplete();

jmethodID mid;
jobject obj;
JNIEnv *jenv;



int crack0(int stop, u32 targ1, u32 targ2, int *pass_ary)
{
  int i, c;
  u32 d, e, sum, step, diff, div, xor1, xor2, state1, state2;
  u32 newstate1, newstate2, newstate3;
  u32 state1_ary[MAX_LEN-2], state2_ary[MAX_LEN-2];
  u32 xor_ary[MAX_LEN-3], step_ary[MAX_LEN-3];
  i = -1;
  sum = 7;
  state1_ary[0] = 1345345333L;
  state2_ary[0] = 0x12345671L;

  while (1) {
      if(isComplete() == true){
	  return NULL;
	  }
    while (i < stop) {
		      if(isComplete() == true){
	  return NULL;
	  }
      i++;
      pass_ary[i] = MIN_CHAR;
      step_ary[i] = (state1_ary[i] & 0x3f) + sum;
      xor_ary[i] = step_ary[i]*MIN_CHAR + (state1_ary[i] << 8);
      sum += MIN_CHAR;
      state1_ary[i+1] = state1_ary[i] ^ xor_ary[i];
      state2_ary[i+1] = state2_ary[i]
        + ((state2_ary[i] << 8) ^ state1_ary[i+1]);
		
    }

    state1 = state1_ary[i+1];
    state2 = state2_ary[i+1];
    step = (state1 & 0x3f) + sum;
    xor1 = step*MIN_CHAR + (state1 << 8);
    xor2 = (state2 << 8) ^ state1;

    for (c = MIN_CHAR; c <= MAX_CHAR; c++, xor1 += step) {
		      if(isComplete() == true){
	  return NULL;
	  }
      newstate2 = state2 + (xor1 ^ xor2);
      newstate1 = state1 ^ xor1;

      newstate3 = (targ2 - newstate2) ^ (newstate2 << 8);
      div = (newstate1 & 0x3f) + sum + c;
      diff = ((newstate3 ^ newstate1) - (newstate1 << 8)) & MASK;
      if (diff % div != 0) continue;
      d = diff / div;
      if (d < MIN_CHAR || d > MAX_CHAR) continue;

      div = (newstate3 & 0x3f) + sum + c + d;
      diff = ((targ1 ^ newstate3) - (newstate3 << 8)) & MASK;
      if (diff % div != 0) continue;
      e = diff / div;
      if (e < MIN_CHAR || e > MAX_CHAR) continue;

      pass_ary[i+1] = c;
      pass_ary[i+2] = d;
      pass_ary[i+3] = e;
      return 1;
    }

    while (i >= 0 && pass_ary[i] >= MAX_CHAR) {
		      if(isComplete() == true){
	  return NULL;
	  }
      sum -= MAX_CHAR;
      i--;
    }
    if (i < 0) break;
    pass_ary[i]++;
    xor_ary[i] += step_ary[i];
    sum++;
    state1_ary[i+1] = state1_ary[i] ^ xor_ary[i];
    state2_ary[i+1] = state2_ary[i]
      + ((state2_ary[i] << 8) ^ state1_ary[i+1]);
  }

  return 0;
}

void crack(char *hash)
{
		  
  int len;
  u32 targ1, targ2, targ3;


  if ( sscanf(hash, "%8lx%lx", &targ1, &targ2) != 2 ) {
    //If hash is invalid
	  pass[13] = 300;
    return;
  }
  targ3 = targ2 - targ1;
  targ3 = targ2 - ((targ3 << 8) ^ targ1);
  targ3 = targ2 - ((targ3 << 8) ^ targ1);
  targ3 = targ2 - ((targ3 << 8) ^ targ1);

  for (len = 3; len <= MAX_LEN; len++) {
      if(isComplete() == true){
	  return;
	  }
    if ( crack0(len-4, targ1, targ3, pass) ) {
		break;
    }
  }
  if (len > MAX_LEN)
    //Password not found
	pass[13] = 400;
}

bool isComplete(){
	return 	jenv->CallBooleanMethod(obj, mid);
}

	JNIEXPORT jintArray JNICALL Java_com_darkprograms_mysqldecrypter_util_HashUtil_getHash (JNIEnv *env, jclass hashutil, jstring stringHash) {
	char* hasher = (char*) env->GetStringUTFChars(stringHash, false);
	
	jintArray passwordArray; 
	jclass cls = hashutil;
	obj = env->AllocObject(cls);
        mid = env->GetMethodID(cls, "complete", "()Z");
	jenv = env;

	crack(hasher);

	if(pass[13] == 300 || pass[13] == 400){
	jintArray failureArray = env->NewIntArray(1); 



	jint buffer[512];

	buffer[0] = pass[13];

	env->SetIntArrayRegion(failureArray, 0, 1, buffer);

	return failureArray;
	}else{

	passwordArray = env->NewIntArray(MAX_LEN); 



	jint buf[512];


	for(int i = 0; i < 12; i++){
	buf[i] = pass[i];
	}

	env->SetIntArrayRegion(passwordArray, 0, MAX_LEN, buf);


	

			return passwordArray;
 
	}

}

