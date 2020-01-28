/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.demo.util.xxhash;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.xxhash.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Objects;
import java.util.Random;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.util.xxhash.XXHash.*;
import static org.testng.Assert.*;

public final class XXHashDemo {

    private XXHashDemo() {
    }

    public static void main(String[] args) {
        ByteBuffer buffer = memAlloc(128);

        Random rand = new Random(13);
        for (int i = 0; i < buffer.capacity(); i++) {
            buffer.put(i, (byte)(rand.nextInt() & 0xFF));
        }

        int SEED = 7;

        // simple functions
        int        hash32  = XXH32(buffer, SEED);
        long       hash64  = XXH64(buffer, SEED);
        XXH128Hash hash128 = XXH128(buffer, SEED, XXH128Hash.create());

        assertEquals(hash32, 0xC5C8C208);
        assertEquals(hash64, 0x656D889F290F0BCCL);
        assertEquals(hash128.high64(), 0x2A3D06D3261fBBC1L);
        assertEquals(hash128.low64(), 0x34BA1D414B6D39F6L);

        System.out.format("test %d-bit hash = 0x%X\n", 32, hash32);
        System.out.format("test %d-bit hash = 0x%X\n", 64, hash64);
        System.out.format("test %d-bit hash = 0x%X%X\n", 128, hash128.high64(), hash128.low64());

        // streaming hash with advanced functions
        String resource = "lwjgl32.png";
        try {
            // Allocate and free using the API
            XXH64State state = Objects.requireNonNull(XXH64_createState());
            try {
                hash64 = streamingHash(buffer, resource, state, SEED);
                System.out.format("streaming 64-bit hash: 0x%X (%s, malloc)\n", hash64, resource);
            } finally {
                XXH64_freeState(state);
            }

            // Using stack allocation
            try (MemoryStack stack = stackPush()) {
                state = XXH64State.mallocStack(stack);
                hash64 = streamingHash(buffer, resource, state, SEED);
                System.out.format("streaming 64-bit hash: 0x%X (%s, stack)\n", hash64, resource);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            memFree(buffer);
        }
    }

    private static long streamingHash(ByteBuffer buffer, String resource, XXH64State state, int SEED) throws IOException {
        // init
        XXH64_reset(state, SEED);
        try (
                InputStream stream = Objects.requireNonNull(XXHashDemo.class.getClassLoader().getResourceAsStream(resource));
                ReadableByteChannel rbc = Channels.newChannel(stream)
        ) {
            while (true) {
                buffer.clear();
                int bytes = rbc.read(buffer);
                if (bytes == -1) {
                    break;
                }

                // stream update
                buffer.flip();
                XXH64_update(state, buffer);
            }
            // digest
            return XXH64_digest(state);
        }
    }

}