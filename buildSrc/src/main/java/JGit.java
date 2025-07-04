import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.IOException;
import java.util.*;

abstract class JGit {

    /**
     * git describe --tags --abbrev=0
     */
    static String describeTags(final Git git) throws GitAPIException, IOException {
        return git.describe().setTags(true).setAbbrev(0).call();
    }

    /**
     * git for-each-ref --sort=-creatordate refs/tags
     */
    static List<VersionTag> listAllTag(final Git git) throws GitAPIException, IOException {
        final var allTag = new ArrayList<VersionTag>();
        final var allTagRef = git.tagList().call();
        for (final var tagRef : allTagRef) {
            final var commit = parseCommit(git, tagRef);
            allTag.add(new VersionTag(tagRef.getName(), commit.getCommitterIdent().getWhenAsInstant()));
        }
        Collections.sort(allTag);
        return allTag;
    }

    /**
     * git log <latest tag>..HEAD --oneline
     */
    static List<String> listAllLog(final Git git, final String fromRef, final String toRef) throws GitAPIException, IOException {
        final var allLog = new ArrayList<String>();
        final var fromId = git.getRepository().resolve(fromRef);
        final var toId = git.getRepository().resolve(toRef);
        final var allCommit = git.log().add(fromId).not(toId).call();
        for (final var commit : allCommit) {
             allLog.add(commit.getShortMessage());
        }
        return allLog;
    }

    static RevCommit parseCommit(final Git git, final String tagName) throws IOException {
        try (final var walk = new RevWalk(git.getRepository())) {
            final var tagRef = git.getRepository().findRef("refs/tags/" + tagName);
            if (tagRef == null) {
                throw new NoSuchElementException("'refs/tags/%s' not found".formatted(tagName));
            }
            return walk.parseCommit(tagRef.getObjectId());
        }
    }

    static RevCommit parseCommit(final Git git, final Ref ref) throws IOException {
        try (final var walk = new RevWalk(git.getRepository())) {
            final var id = ref.getObjectId();
            final var rev = walk.parseAny(id);
            if (rev instanceof RevTag revTag) {
                return walk.parseCommit(walk.peel(revTag));
            } else {
                return walk.parseCommit(id);
            }
        }
    }
}
