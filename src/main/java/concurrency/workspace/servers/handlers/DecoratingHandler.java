package concurrency.workspace.servers.handlers;

import java.util.function.Consumer;

abstract class DecoratingHandler<S> implements Consumer<S> {
  private final Consumer<S> other;

  public DecoratingHandler(Consumer<S> other) {
    this.other = other;
  }

  @Override
  public void accept(S s) {
    other.accept(s);
  }
}