package edu.comp90051.l0sampler;

public interface L0Sampler {

  void update(int item, int delta);

  Integer output() throws FailToRetrieveException;
}
