package de.m_marvin.genet.misc;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.mojang.datafixers.util.Pair;

public class StreamExtras {
	
	public static <A,B> Stream<Pair<A,B>> costream(Stream<A> s1, Stream<B> s2) {
		Iterator<B> iter = s2.iterator();
		return StreamSupport.stream(s1.spliterator(), false).map(a -> new Pair<A, B>(a, iter.next()));
	}
	
}
