/*
Copyright (C) 1997-2001 Id Software, Inc.

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

*/
/* Modifications
   Copyright 2003-2004 Bytonic Software
   Copyright 2010 Google Inc.
*/
package com.googlecode.gwtquake.shared.common;

import java.util.Arrays;

import com.googlecode.gwtquake.shared.util.Lib;

/**
 * sizebuf_t
 */
public final class Buffer {
	public boolean allowoverflow = false;
	public boolean overflowed = false;
	public byte[] data = null;
	public int maxsize = 0;
	public int cursize = 0;
	public int readcount = 0;
	
	public void clear()
	{
		if (data!=null)		
			Arrays.fill(data,(byte)0);
		cursize = 0;
		overflowed = false;
	}

	// 
	public static void Print(Buffer buf, String data) {
	    Com.dprintln("SZ.print():<" + data + ">" );
		int length = data.length();
		byte str[] = Lib.stringToBytes(data);
	
		if (buf.cursize != 0) {
	
			if (buf.data[buf.cursize - 1] != 0) {
				//memcpy( SZ_GetSpace(buf, len), data, len); // no trailing 0
				System.arraycopy(str, 0, buf.data, GetSpace(buf, length+1), length);
			} else {
				System.arraycopy(str, 0, buf.data, GetSpace(buf, length)-1, length);
				//memcpy(SZ_GetSpace(buf, len - 1) - 1, data, len); // write over trailing 0
			}
		} else
			// first print.
			System.arraycopy(str, 0, buf.data, GetSpace(buf, length), length);
		//memcpy(SZ_GetSpace(buf, len), data, len);
		
		buf.data[buf.cursize - 1]=0;
	}

	public static void Write(Buffer buf, byte data[]) {
		int length = data.length;
		//memcpy(SZ_GetSpace(buf, length), data, length);
		System.arraycopy(data, 0, buf.data, GetSpace(buf, length), length);
	}

	public static void Write(Buffer buf, byte data[], int offset, int length) {
		System.arraycopy(data, offset, buf.data, GetSpace(buf, length), length);
	}

	public static void Write(Buffer buf, byte data[], int length) {
		//memcpy(SZ_GetSpace(buf, length), data, length);
		System.arraycopy(data, 0, buf.data, GetSpace(buf, length), length);
	}

	/** Ask for the pointer using sizebuf_t.cursize (RST) */
	public static int GetSpace(Buffer buf, int length) {
		int oldsize;
	
		if (buf.cursize + length > buf.maxsize) {
			if (!buf.allowoverflow)
				Com.Error(Defines.ERR_FATAL, "SZ_GetSpace: overflow without allowoverflow set");
	
			if (length > buf.maxsize)
				Com.Error(Defines.ERR_FATAL, "SZ_GetSpace: " + length + " is > full buffer size");
	
			Com.Printf("SZ_GetSpace: overflow\n");
			buf.clear();
			buf.overflowed = true;
		}
	
		oldsize = buf.cursize;
		buf.cursize += length;
	
		return oldsize;
	}

	public static void Init(Buffer buf, byte data[], int length) {
	  // TODO check this. cwei
	  buf.readcount = 0;
	
	  buf.data = data;
		buf.maxsize = length;
		buf.cursize = 0;
		buf.allowoverflow = buf.overflowed = false;
	}
}
