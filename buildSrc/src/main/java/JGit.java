import lombok.NonNull;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;

public final class JGit implements AutoCloseable {

    private final Git git;

    private JGit(@NonNull final Git git) {
        this.git = git;
    }

    public static JGit open(@NonNull final File rootDir) {
        try {
            return new JGit(Git.open(rootDir));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void close() {
        this.git.close();
    }

    /**
     * git describe --tags --abbrev=0
     */
    public Optional<VersionTag> releaseTag() {
        try {
            final var tagName = "refs/tags/" + git.describe().setTags(true).setAbbrev(0).call();
            final var tagRef = git.getRepository().findRef(tagName);
            if (tagRef == null) {
                throw new NoSuchElementException("'%s' not found".formatted(tagName));
            }
            final var tagCommit = parseCommit(tagRef);
            final var commitAt = tagCommit.getCommitterIdent().getWhenAsInstant();
            return VersionTag.cleanTag(tagName, commitAt);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (GitAPIException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    /**
     * git describe --tags --abbrev=0
     */
    public Optional<VersionTag> versionTag() {
        try {
            final var tagName = "refs/tags/" + git.describe().setTags(true).setAbbrev(0).call();
            final var tagRef = git.getRepository().findRef(tagName);
            if (tagRef == null) {
                throw new NoSuchElementException("'%s' not found".formatted(tagName));
            }
            final var tagCommit = parseCommit(tagRef);
            final var fromId = git.getRepository().resolve("HEAD");;
            final var toId = tagCommit.getId();
            final var allRevCommit = git.log().add(fromId).not(toId).call();
            if (allRevCommit.iterator().hasNext()) {
                return VersionTag.dirtyTag(tagName);
            } else {
                final var commitAt = tagCommit.getCommitterIdent().getWhenAsInstant();
                return VersionTag.cleanTag(tagName, commitAt);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (GitAPIException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    /**
     * git for-each-ref --sort=-creatordate refs/tags
     */
    public List<VersionTag> listAllTag() {
        try {
            final var allTag = new ArrayList<VersionTag>();
            final var allTagRef = git.tagList().call();
            for (final var tagRef : allTagRef) {
                final var tagName = tagRef.getName();
                final var tagCommit = parseCommit(tagRef);
                final var commitAt = tagCommit.getCommitterIdent().getWhenAsInstant();
                VersionTag.cleanTag(tagName, commitAt).ifPresent(allTag::add);
            }
            Collections.sort(allTag);
            return allTag;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (GitAPIException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    /**
     * git log tag1..tag2 --oneline
     */
    public List<String> listAllLog(@NonNull final String fromRef, @NonNull final String toRef) {
        try {
            final var allLog = new ArrayList<String>();
            final var fromId = git.getRepository().resolve(fromRef);
            final var toId = git.getRepository().resolve(toRef);
            final var allRevCommit = git.log().add(fromId).not(toId).call();
            for (final var revCommit : allRevCommit) {
                allLog.add(revCommit.getShortMessage());
            }
            return allLog;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (GitAPIException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    private RevCommit parseCommit(@NonNull final Ref tagRef) throws IOException {
        try (final var walk = new RevWalk(git.getRepository())) {
            final var id = tagRef.getObjectId();
            final var rev = walk.parseAny(id);
            if (rev instanceof RevTag revTag) {
                return walk.parseCommit(walk.peel(revTag));
            } else {
                return walk.parseCommit(id);
            }
        }
    }
}
