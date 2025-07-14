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
import java.nio.file.Files;
import java.time.Instant;
import java.util.*;

public final class JGit implements AutoCloseable {

    private static final MessageTester messageTester = new MessageTester();

    private static final VersionTester versionTester = new VersionTester();

    private final Git git;

    private final VersionTag localTag;

    private JGit(@NonNull final Git git, @NonNull final VersionTag localTag) {
        this.git = git;
        this.localTag = localTag;
    }

    public static JGit open(@NonNull final File rootDir, @NonNull final File version) {
        try {
            final var tagName = Files.readString(version.toPath());
            final var commitAt = Instant.ofEpochMilli(version.lastModified());
            final var localTag = new VersionTag(versionTester.apply(tagName), commitAt);
            final var git = Git.open(rootDir);
            return new JGit(git, localTag);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void close() {
        this.git.close();
    }

    public VersionTag versionTag() {
        return localTag;
    }

    /**
     * git describe --tags --abbrev=0
     */
    public VersionTag releaseTag() {
        try {
            final var tagName = git.describe().setTags(true).setAbbrev(0).call();
            if (tagName == null) {
                throw new IllegalStateException("tags not available");
            }
            final var tagRef = git.getRepository().findRef("refs/tags/" + tagName);
            if (tagRef == null) {
                throw new NoSuchElementException("tag '%s' not found".formatted(tagName));
            }
            final var tagCommit = parseCommit(tagRef);
            final var commitAt = tagCommit.getCommitterIdent().getWhenAsInstant();
            final var semVer = versionTester.apply(tagName);
            return new VersionTag(semVer, commitAt);
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
                final var tagName = tagRef.getName().replace("refs/tags/", "");
                final var tagCommit = parseCommit(tagRef);
                final var commitAt = tagCommit.getCommitterIdent().getWhenAsInstant();
                final var cleanTag = new VersionTag(versionTester.apply(tagName), commitAt);
                if (cleanTag.isValid() && !cleanTag.followsAfter(localTag)) {
                    allTag.add(cleanTag);
                }
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
     * git log fromRef..toRef --oneline
     */
    public List<Message> listAllLog(@NonNull final String fromRef, @NonNull final String toRef) {
        try {
            final var allLog = new ArrayList<Message>();
            final var fromId = git.getRepository().resolve(fromRef);
            final var toId = git.getRepository().resolve(toRef);
            final var allRevCommit = git.log().add(fromId).not(toId).call();
            for (final var revCommit : allRevCommit) {
                final var message = revCommit.getShortMessage();
                allLog.add(messageTester.apply(message));
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
