package com.prostudios.client.util;

import java.util.ArrayDeque;
import java.util.Deque;

public final class CpsTracker {
	private static final Deque<Long> LEFT = new ArrayDeque<>();
	private static final Deque<Long> RIGHT = new ArrayDeque<>();
	private static final long WINDOW_MS = 1000;

	private CpsTracker() {}

	public static void onPress(int button) {
		(button == 0 ? LEFT : RIGHT).addLast(System.currentTimeMillis());
	}

	public static int left() {
		return count(LEFT);
	}

	public static int right() {
		return count(RIGHT);
	}

	private static int count(Deque<Long> deque) {
		long now = System.currentTimeMillis();
		while (!deque.isEmpty() && now - deque.peekFirst() > WINDOW_MS) deque.pollFirst();
		return deque.size();
	}
}
