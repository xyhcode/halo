package run.halo.app.theme.finders.impl;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ObjectUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.halo.app.core.extension.Tag;
import run.halo.app.extension.ListResult;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.theme.finders.Finder;
import run.halo.app.theme.finders.TagFinder;
import run.halo.app.theme.finders.vo.TagVo;

/**
 * A default implementation of {@link TagFinder}.
 *
 * @author guqing
 * @since 2.0.0
 */
@Finder("tagFinder")
public class TagFinderImpl implements TagFinder {

    public static final Comparator<Tag> DEFAULT_COMPARATOR =
        Comparator.comparing(tag -> tag.getMetadata().getCreationTimestamp());

    private final ReactiveExtensionClient client;

    public TagFinderImpl(ReactiveExtensionClient client) {
        this.client = client;
    }

    @Override
    public Mono<TagVo> getByName(String name) {
        return client.fetch(Tag.class, name)
            .map(TagVo::from);
    }

    @Override
    public Flux<TagVo> getByNames(List<String> names) {
        return Flux.fromIterable(names)
            .concatMap(this::getByName);
    }

    @Override
    public Mono<ListResult<TagVo>> list(Integer page, Integer size) {
        return client.list(Tag.class, null,
                DEFAULT_COMPARATOR.reversed(), pageNullSafe(page), sizeNullSafe(size))
            .map(list -> {
                List<TagVo> tagVos = list.stream()
                    .map(TagVo::from)
                    .collect(Collectors.toList());
                return new ListResult<>(list.getPage(), list.getSize(), list.getTotal(), tagVos);
            })
            .defaultIfEmpty(new ListResult<>(page, size, 0L, List.of()));
    }

    @Override
    public Flux<TagVo> listAll() {
        return client.list(Tag.class, null,
                DEFAULT_COMPARATOR.reversed())
            .map(TagVo::from);
    }

    int pageNullSafe(Integer page) {
        return ObjectUtils.defaultIfNull(page, 1);
    }

    int sizeNullSafe(Integer size) {
        return ObjectUtils.defaultIfNull(size, 10);
    }
}
