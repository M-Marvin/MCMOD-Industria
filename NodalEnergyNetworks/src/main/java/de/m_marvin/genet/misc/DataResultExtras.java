package de.m_marvin.genet.misc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.mojang.serialization.DataResult;

public class DataResultExtras {
	
	private DataResultExtras() {}
	
	public static <A, B> Function<A, DataResult<B>> unstableNonNullMap(UnstableFunction<A, B> mapper) {
		return a -> {
			try {
				B b = mapper.apply(a);
				return b == null ? DataResult.error(() -> "") : DataResult.success(b);
			} catch (Exception e) {
				return DataResult.error(() -> e.getMessage());
			}
		};
	}

	public static <A, B> Function<A, DataResult<B>> unstableMap(UnstableFunction<A, B> mapper) {
		return a -> {
			try {
				return DataResult.success(mapper.apply(a));
			} catch (Exception e) {
				return DataResult.error(() -> e.getMessage());
			}
		};
	}
	
	public static <T> DataResult<T> unstableNonNull(UnstableSupplier<T> supplier) {
		try {
			T result = supplier.get();
			return result == null ? DataResult.error(() -> "result is null") : DataResult.success(result);
		} catch (Exception e) {
			return DataResult.error(e::getMessage);
		}
	}
	
	public static <T> DataResult<T> unstable(UnstableSupplier<T> supplier) {
		try {
			T result = supplier.get();
			return DataResult.success(result);
		} catch (Exception e) {
			return DataResult.error(e::getMessage);
		}
	}
	
	public static <T> DataResult<Stream<T>> flatStream(Stream<DataResult<T>> stream) {
		Iterator<DataResult<T>> iter = stream.iterator();
		List<T> list = new ArrayList<T>();
		for (int i = 0; iter.hasNext(); i++) {
			DataResult<T> result =  iter.next();
			if (result.isError()) {
				String msg = "unable to flatten result stream, non success entry: " + i + " -> " + result.error().get().message();
				return DataResult.error(() -> msg, list.stream());
			} else {
				list.add(result.result().get());
			}
		}
		return DataResult.success(list.stream());
	}
	
}
